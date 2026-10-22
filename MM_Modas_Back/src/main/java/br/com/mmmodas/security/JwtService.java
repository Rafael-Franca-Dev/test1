package br.com.mmmodas.security;

import br.com.mmmodas.config.JwtProperties;
import br.com.mmmodas.domain.Cliente;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/**
 * Emissao e leitura dos tokens.
 *
 * SAO DOIS TOKENS COM PAPEIS DIFERENTES:
 *
 *  - access token: vida curta (30 min). Vai no header Authorization de toda
 *    requisicao. Se vazar, o estrago tem prazo.
 *  - refresh token: vida longa (7 dias). Serve SO para pedir um access novo.
 *
 * Os dois sao assinados com a mesma chave, entao a claim "typ" e o que os
 * distingue. Sem essa marcacao, um refresh token (que dura 7 dias) poderia ser
 * apresentado como access token e a vida curta do access nao valeria nada.
 * Por isso a validacao de access rejeita explicitamente typ != "access".
 */
@Service
public class JwtService {

    private static final String TIPO_ACCESS = "access";
    private static final String TIPO_REFRESH = "refresh";
    private static final String CLAIM_TIPO = "typ";
    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_NOME = "nome";
    private static final String CLAIM_PERFIL = "perfil";

    private final SecretKey chave;
    private final long accessSegundos;
    private final long refreshSegundos;

    public JwtService(JwtProperties propriedades) {
        this.chave = Keys.hmacShaKeyFor(propriedades.secret().getBytes(StandardCharsets.UTF_8));
        this.accessSegundos = propriedades.accessTokenSegundos();
        this.refreshSegundos = propriedades.refreshTokenDias() * 24 * 60 * 60;
    }

    public String gerarAccessToken(Cliente cliente) {
        return gerar(cliente, TIPO_ACCESS, accessSegundos);
    }

    public String gerarRefreshToken(Cliente cliente) {
        return gerar(cliente, TIPO_REFRESH, refreshSegundos);
    }

    public long accessTokenSegundos() {
        return accessSegundos;
    }

    private String gerar(Cliente cliente, String tipo, long validadeSegundos) {
        Instant agora = Instant.now();
        return Jwts.builder()
                .subject(cliente.getId().toString())
                .claim(CLAIM_TIPO, tipo)
                .claim(CLAIM_EMAIL, cliente.getEmail())
                .claim(CLAIM_NOME, cliente.getNome())
                .claim(CLAIM_PERFIL, cliente.getPerfil().name())
                .issuedAt(Date.from(agora))
                .expiration(Date.from(agora.plusSeconds(validadeSegundos)))
                .signWith(chave)
                .compact();
    }

    /**
     * Valida assinatura e validade do ACCESS token.
     *
     * @throws JwtException se invalido, expirado ou se for um refresh token.
     */
    public Claims validarAccessToken(String token) {
        Claims claims = extrairClaims(token);
        if (!TIPO_ACCESS.equals(claims.get(CLAIM_TIPO, String.class))) {
            throw new JwtException("Token nao e um access token.");
        }
        return claims;
    }

    /** Valida assinatura e validade do REFRESH token. */
    public Claims validarRefreshToken(String token) {
        Claims claims = extrairClaims(token);
        if (!TIPO_REFRESH.equals(claims.get(CLAIM_TIPO, String.class))) {
            throw new JwtException("Token nao e um refresh token.");
        }
        return claims;
    }

    private Claims extrairClaims(String token) {
        // verifyWith valida a assinatura; parseSignedClaims recusa token expirado
        // lancando ExpiredJwtException.
        return Jwts.parser()
                .verifyWith(chave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public UsuarioAutenticado paraUsuario(Claims claims) {
        return new UsuarioAutenticado(
                UUID.fromString(claims.getSubject()),
                claims.get(CLAIM_EMAIL, String.class),
                claims.get(CLAIM_NOME, String.class),
                br.com.mmmodas.domain.enums.PerfilUsuario.valueOf(
                        claims.get(CLAIM_PERFIL, String.class))
        );
    }
}
