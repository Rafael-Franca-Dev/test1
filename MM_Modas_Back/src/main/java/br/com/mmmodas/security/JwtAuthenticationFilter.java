package br.com.mmmodas.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Le o header "Authorization: Bearer &lt;token&gt;" e popula o contexto de seguranca.
 *
 * Se o token for invalido ou expirado, NAO lanca erro aqui: apenas deixa a
 * requisicao seguir sem autenticacao. Quem responde 401 e o entry point do
 * SecurityConfig, que devolve JSON no mesmo formato dos outros erros. Se este
 * filtro lancasse excecao, o cliente receberia um 500 com stacktrace em vez de
 * um 401 tratado.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String PREFIXO = "Bearer ";

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith(PREFIXO)
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            String token = header.substring(PREFIXO.length()).trim();
            try {
                Claims claims = jwtService.validarAccessToken(token);
                UsuarioAutenticado usuario = jwtService.paraUsuario(claims);

                // hasRole("ADMIN") no SecurityConfig espera a authority ROLE_ADMIN.
                var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + usuario.perfil().name()));

                var autenticacao = new UsernamePasswordAuthenticationToken(usuario, null, authorities);
                autenticacao.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(autenticacao);

            } catch (JwtException | IllegalArgumentException ex) {
                log.debug("Token rejeitado: {}", ex.getMessage());
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}
