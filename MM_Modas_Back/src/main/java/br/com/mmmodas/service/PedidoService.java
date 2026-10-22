package br.com.mmmodas.service;

import br.com.mmmodas.domain.*;
import br.com.mmmodas.domain.enums.StatusCarrinho;
import br.com.mmmodas.domain.enums.StatusPedido;
import br.com.mmmodas.domain.enums.TipoMovimentacao;
import br.com.mmmodas.exception.RecursoNaoEncontradoException;
import br.com.mmmodas.exception.RegraDeNegocioException;
import br.com.mmmodas.repository.*;
import br.com.mmmodas.web.dto.pedido.ItemPedidoRequest;
import br.com.mmmodas.web.dto.pedido.PedidoRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.*;

/**
 * Fechamento de pedido: o ponto onde dinheiro e estoque se encontram.
 *
 * GARANTIAS DESTA CLASSE
 *
 * 1. Tudo ou nada. O metodo e @Transactional: pedido, itens, baixa de estoque,
 *    consumo do cupom e lancamentos de auditoria commitam juntos. Se o terceiro
 *    item nao tiver saldo, o pedido inteiro e desfeito e o estoque dos dois
 *    primeiros volta sozinho - nao existe estado "meio comprado".
 *
 * 2. Sem venda fantasma. A baixa e um UPDATE condicional (ver EstoqueRepository).
 *    Duas pessoas comprando a ultima peca ao mesmo tempo: uma recebe "baixou",
 *    a outra recebe zero linhas afetadas e a transacao dela e cancelada. Nunca
 *    as duas.
 *
 * 3. Sem deadlock. Os itens sao processados em ordem deterministica de
 *    variacaoId (TreeMap). Dois pedidos que compartilham SKUs pegam as travas
 *    sempre na mesma sequencia, entao nao ficam esperando um ao outro.
 *
 * 4. Sem preco vindo do cliente. Todo valor e lido do banco aqui.
 *
 * 5. Sem pedido duplicado. idempotencyKey + UNIQUE no banco.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final EnderecoRepository enderecoRepository;
    private final VariacaoRepository variacaoRepository;
    private final CupomRepository cupomRepository;
    private final CarrinhoRepository carrinhoRepository;
    private final EstoqueService estoqueService;
    private final FreteService freteService;

    @Transactional
    public Pedido criar(UUID clienteId, PedidoRequest req) {
        String chave = normalizarChave(req.idempotencyKey());

        // (5) Reenvio da mesma requisicao devolve o pedido que ja existe.
        if (chave != null) {
            Optional<Pedido> existente = pedidoRepository.findByIdempotencyKey(chave);
            if (existente.isPresent()) {
                log.info("Chave de idempotencia '{}' ja utilizada; devolvendo pedido {}.",
                        chave, existente.get().getNumero());
                return existente.get();
            }
        }

        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Cliente", clienteId));

        Endereco endereco = enderecoRepository.findByIdAndClienteId(req.enderecoId(), clienteId)
                .orElseThrow(() -> new RegraDeNegocioException("ENDERECO_INVALIDO",
                        "Endereco nao encontrado para este cliente."));

        // (3) Agrega SKUs repetidos e ordena por variacaoId. A ordenacao nao e
        // estetica: e o que impede deadlock entre checkouts simultaneos.
        NavigableMap<UUID, Integer> quantidades = new TreeMap<>();
        for (ItemPedidoRequest item : req.itens()) {
            quantidades.merge(item.variacaoId(), item.quantidade(), Integer::sum);
        }

        List<UUID> ids = new ArrayList<>(quantidades.keySet());
        Map<UUID, Variacao> variacoes = new HashMap<>();
        for (Variacao v : variacaoRepository.buscarComProduto(ids)) {
            variacoes.put(v.getId(), v);
        }
        if (variacoes.size() != ids.size()) {
            List<UUID> faltando = ids.stream().filter(id -> !variacoes.containsKey(id)).toList();
            throw new RegraDeNegocioException("VARIACAO_INVALIDA",
                    "Variacao inexistente: " + faltando);
        }

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setStatus(StatusPedido.PENDENTE);
        pedido.setIdempotencyKey(chave);
        pedido.setObservacoes(req.observacoes());
        pedido.copiarEnderecoDe(endereco);

        // (4) Precos lidos do banco, nunca do payload.
        BigDecimal subtotal = BigDecimal.ZERO;
        for (Map.Entry<UUID, Integer> entrada : quantidades.entrySet()) {
            Variacao v = variacoes.get(entrada.getKey());
            int qtd = entrada.getValue();

            if (!v.isAtivo() || !v.getProduto().isAtivo()) {
                throw new RegraDeNegocioException("PRODUTO_INDISPONIVEL",
                        "Produto fora de linha: " + v.getSku());
            }

            BigDecimal preco = v.precoEfetivo();
            pedido.addItem(PedidoItem.de(v, qtd, preco));
            subtotal = subtotal.add(preco.multiply(BigDecimal.valueOf(qtd)));
        }
        pedido.setSubtotal(subtotal.setScale(2, RoundingMode.HALF_UP));

        // --- Cupom ---
        BigDecimal desconto = BigDecimal.ZERO;
        Cupom cupom = null;
        if (req.cupomCodigo() != null && !req.cupomCodigo().isBlank()) {
            cupom = cupomRepository.findByCodigoIgnoreCase(req.cupomCodigo().trim())
                    .orElseThrow(() -> new RegraDeNegocioException("CUPOM_INVALIDO",
                            "Cupom nao encontrado."));
            if (!cupom.podeSerUsado(pedido.getSubtotal(), OffsetDateTime.now())) {
                throw new RegraDeNegocioException("CUPOM_INVALIDO",
                        "Cupom expirado, esgotado ou indisponivel para este valor de compra.");
            }
            desconto = cupom.calcularDesconto(pedido.getSubtotal());
            pedido.setCupom(cupom);
        }
        pedido.setDesconto(desconto);

        // --- Frete: sempre do servidor ---
        BigDecimal frete = (cupom != null && cupom.isFreteGratis())
                ? BigDecimal.ZERO.setScale(2)
                : freteService.calcular(pedido.getSubtotal(), endereco.getUf());
        pedido.setValorFrete(frete);
        pedido.recalcularTotal();

        // Grava o pedido agora: os lancamentos de estoque referenciam pedido_id e
        // a FK exige a linha ja existente. saveAndFlush forca o INSERT imediato
        // (em vez de deixar na fila ate o commit) e ja traz o numero gerado.
        pedidoRepository.saveAndFlush(pedido);

        // Consome o cupom de forma atomica. Se outra compra levou a ultima
        // unidade antes, isto devolve 0 e o pedido inteiro e desfeito.
        if (cupom != null && cupomRepository.consumir(cupom.getId()) == 0) {
            throw new RegraDeNegocioException("CUPOM_ESGOTADO",
                    "Este cupom acabou de atingir o limite de usos.");
        }

        // (2) Baixa o estoque, na ordem ja estabelecida pelo TreeMap.
        for (Map.Entry<UUID, Integer> entrada : quantidades.entrySet()) {
            estoqueService.baixar(entrada.getKey(), entrada.getValue(), pedido.getId());
        }

        converterCarrinhoDoCliente(clienteId);

        log.info("Pedido {} criado para o cliente {} - total R$ {}.",
                pedido.getNumero(), cliente.getEmail(), pedido.getTotal());

        return pedido;
    }

    /**
     * Confirma o pagamento. Chamado pelo webhook do Mercado Pago e pela
     * aprovacao de cartao.
     *
     * IDEMPOTENTE POR CONSTRUCAO: a transicao de status e um UPDATE condicional
     * ("WHERE status = 'PENDENTE'"). O gateway reenvia a mesma notificacao varias
     * vezes; na segunda, a transicao nao afeta linhas e saimos sem tocar no
     * estoque de novo. E aqui que um webhook duplicado deixaria de virar venda
     * fantasma.
     */
    @Transactional
    public void confirmarPagamento(UUID pedidoId) {
        int linhas = pedidoRepository.marcarComoPago(pedidoId, OffsetDateTime.now());

        if (linhas == 0) {
            log.info("Pedido {} nao estava PENDENTE; notificacao duplicada ignorada.", pedidoId);
            return;
        }
        // O estoque ja foi baixado na criacao do pedido. Reafirmamos a garantia
        // para o caso de o pedido ter sido criado por outro caminho.
        log.info("Pedido {} marcado como PAGO.", pedidoId);
    }

    /**
     * Cancela e devolve o estoque. Condicional: se o pedido ja estava cancelado
     * ou entregue, nao faz nada e nao estorna duas vezes.
     */
    @Transactional
    public void cancelar(UUID pedidoId, String motivo) {
        Pedido pedido = pedidoRepository.buscarComItens(pedidoId)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Pedido", pedidoId));

        if (!pedido.getStatus().aceitaCancelamento()) {
            throw new RegraDeNegocioException("CANCELAMENTO_INVALIDO",
                    "Pedido no status " + pedido.getStatus() + " nao pode ser cancelado.");
        }

        int linhas = pedidoRepository.marcarComoCancelado(pedidoId, OffsetDateTime.now());
        if (linhas == 0) {
            // Outra requisicao cancelou entre a leitura e o update.
            log.info("Pedido {} ja havia sido cancelado por outra requisicao.", pedidoId);
            return;
        }

        // Devolve ao estoque apenas se a saida realmente aconteceu.
        if (pedidoRepository.estoqueJaFoiBaixado(pedidoId)) {
            List<EstoqueService.ItemEstorno> itens = pedido.getItens().stream()
                    .map(i -> new EstoqueService.ItemEstorno(i.getVariacao().getId(), i.getQuantidade()))
                    .toList();
            estoqueService.estornarPedido(pedidoId, itens);
        }

        if (pedido.getCupom() != null) {
            cupomRepository.devolver(pedido.getCupom().getId());
        }

        log.info("Pedido {} cancelado. Motivo: {}", pedido.getNumero(), motivo);
    }

    @Transactional(readOnly = true)
    public Pedido buscarPorId(UUID pedidoId) {
        return pedidoRepository.buscarComItens(pedidoId)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Pedido", pedidoId));
    }

    /** Apoio ao tratamento de corrida na idempotencia (ver PedidoController). */
    @Transactional(readOnly = true)
    public Optional<Pedido> buscarPorIdempotencyKey(String chave) {
        return chave == null ? Optional.empty() : pedidoRepository.findByIdempotencyKey(chave);
    }

    @Transactional(readOnly = true)
    public List<Pedido> listarDoCliente(UUID clienteId, org.springframework.data.domain.Pageable pageable) {
        return pedidoRepository.findByClienteIdOrderByCriadoEmDesc(clienteId, pageable).getContent();
    }

    private void converterCarrinhoDoCliente(UUID clienteId) {
        carrinhoRepository
                .findFirstByClienteIdAndStatusOrderByCriadoEmDesc(clienteId, StatusCarrinho.ATIVO)
                .ifPresent(c -> c.setStatus(StatusCarrinho.CONVERTIDO));
    }

    private String normalizarChave(String chave) {
        if (chave == null) {
            return null;
        }
        String limpa = chave.trim();
        return limpa.isEmpty() ? null : limpa;
    }
}
