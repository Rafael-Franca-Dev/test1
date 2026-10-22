package br.com.mmmodas.service;

import br.com.mmmodas.domain.Cliente;
import br.com.mmmodas.domain.enums.PerfilUsuario;
import br.com.mmmodas.exception.ConflitoException;
import br.com.mmmodas.exception.NaoAutorizadoException;
import br.com.mmmodas.exception.RecursoNaoEncontradoException;
import br.com.mmmodas.repository.ClienteRepository;
import br.com.mmmodas.security.JwtService;
import br.com.mmmodas.web.dto.auth.*;
import br.com.mmmodas.web.dto.endereco.EnderecoResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final ClienteRepository clienteRepository;
    private final EnderecoService enderecoService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse registrar(RegistroRequest req) {
        String emailNormalizado = req.email().trim().toLowerCase();
        if (clienteRepository.existsByEmailIgnoreCase(emailNormalizado)) {
            throw new ConflitoException("Ja existe uma conta cadastrada com este e-mail.");
        }

        String cpfLimpo = req.cpf() != null && !req.cpf().isBlank()
                ? req.cpf().replaceAll("\\D", "")
                : null;
        if (cpfLimpo != null && clienteRepository.existsByCpf(cpfLimpo)) {
            throw new ConflitoException("Ja existe uma conta cadastrada com este CPF.");
        }

        Cliente cliente = new Cliente();
        cliente.setNome(req.nome().trim());
        cliente.setEmail(emailNormalizado);
        cliente.setSenhaHash(passwordEncoder.encode(req.senha()));
        cliente.setCpf(cpfLimpo);
        cliente.setTelefone(req.telefone() != null ? req.telefone().trim() : null);
        cliente.setPerfil(PerfilUsuario.CLIENTE);
        cliente.setAtivo(true);

        cliente = clienteRepository.save(cliente);

        EnderecoResponse enderecoPrincipal = null;
        if (req.endereco() != null) {
            enderecoPrincipal = enderecoService.criar(cliente.getId(), req.endereco());
        }

        return emitirTokens(cliente, enderecoPrincipal);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        String emailNormalizado = req.email().trim().toLowerCase();
        Cliente cliente = clienteRepository.findByEmailIgnoreCase(emailNormalizado)
                .orElseThrow(() -> new NaoAutorizadoException("E-mail ou senha invalidos."));

        if (!cliente.isAtivo() || !passwordEncoder.matches(req.senha(), cliente.getSenhaHash())) {
            throw new NaoAutorizadoException("E-mail ou senha invalidos.");
        }

        EnderecoResponse enderecoPrincipal = enderecoService.buscarPrincipalDoCliente(cliente.getId()).orElse(null);
        return emitirTokens(cliente, enderecoPrincipal);
    }

    @Transactional(readOnly = true)
    @SuppressWarnings("null")
    public AuthResponse refresh(RefreshRequest req) {
        try {
            Claims claims = jwtService.validarRefreshToken(req.refreshToken());
            UUID clienteId = UUID.fromString(claims.getSubject());
            Cliente cliente = clienteRepository.findById(clienteId)
                    .orElseThrow(() -> new NaoAutorizadoException("Usuario nao encontrado."));
            if (!cliente.isAtivo()) {
                throw new NaoAutorizadoException("Conta desativada.");
            }
            EnderecoResponse enderecoPrincipal = enderecoService.buscarPrincipalDoCliente(cliente.getId()).orElse(null);
            return emitirTokens(cliente, enderecoPrincipal);
        } catch (JwtException | IllegalArgumentException ex) {
            throw new NaoAutorizadoException("Refresh token invalido ou expirado.");
        }
    }

    @Transactional(readOnly = true)
    @SuppressWarnings("null")
    public UsuarioResponse buscarUsuarioAtual(UUID clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Cliente", clienteId));
        EnderecoResponse enderecoPrincipal = enderecoService.buscarPrincipalDoCliente(clienteId).orElse(null);
        return UsuarioResponse.de(cliente, enderecoPrincipal);
    }

    private AuthResponse emitirTokens(Cliente cliente, EnderecoResponse enderecoPrincipal) {
        String access = jwtService.gerarAccessToken(cliente);
        String refresh = jwtService.gerarRefreshToken(cliente);
        return new AuthResponse(
                access,
                refresh,
                jwtService.accessTokenSegundos(),
                UsuarioResponse.de(cliente, enderecoPrincipal)
        );
    }
}

