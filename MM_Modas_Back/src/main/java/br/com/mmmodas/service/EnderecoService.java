package br.com.mmmodas.service;

import br.com.mmmodas.domain.Cliente;
import br.com.mmmodas.domain.Endereco;
import br.com.mmmodas.exception.RecursoNaoEncontradoException;
import br.com.mmmodas.repository.ClienteRepository;
import br.com.mmmodas.repository.EnderecoRepository;
import br.com.mmmodas.web.dto.endereco.EnderecoRequest;
import br.com.mmmodas.web.dto.endereco.EnderecoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EnderecoService {

    private final EnderecoRepository enderecoRepository;
    private final ClienteRepository clienteRepository;

    @Transactional(readOnly = true)
    public List<EnderecoResponse> listarDoCliente(UUID clienteId) {
        return enderecoRepository.findByClienteIdOrderByPrincipalDescCriadoEmDesc(clienteId)
                .stream()
                .map(EnderecoResponse::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<EnderecoResponse> buscarPrincipalDoCliente(UUID clienteId) {
        return enderecoRepository.findByClienteIdOrderByPrincipalDescCriadoEmDesc(clienteId)
                .stream()
                .findFirst()
                .map(EnderecoResponse::de);
    }

    @Transactional
    public EnderecoResponse criar(UUID clienteId, EnderecoRequest req) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Cliente", clienteId));

        boolean temEnderecos = enderecoRepository.countByClienteId(clienteId) > 0;
        boolean definirComoPrincipal = Boolean.TRUE.equals(req.principal()) || !temEnderecos;

        if (definirComoPrincipal && temEnderecos) {
            enderecoRepository.limparPrincipal(clienteId);
        }

        Endereco endereco = new Endereco();
        endereco.setCliente(cliente);
        endereco.setApelido(req.apelido() != null ? req.apelido().trim() : "Principal");
        endereco.setDestinatario(req.destinatario().trim());
        endereco.setCep(req.cep().replaceAll("\\D", ""));
        endereco.setLogradouro(req.logradouro().trim());
        endereco.setNumero(req.numero().trim());
        endereco.setComplemento(req.complemento() != null && !req.complemento().isBlank() ? req.complemento().trim() : null);
        endereco.setBairro(req.bairro().trim());
        endereco.setCidade(req.cidade().trim());
        endereco.setUf(req.uf().trim().toUpperCase());
        endereco.setPrincipal(definirComoPrincipal);

        return EnderecoResponse.de(enderecoRepository.save(endereco));
    }
}

