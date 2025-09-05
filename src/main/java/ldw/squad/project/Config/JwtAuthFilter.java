package ldw.squad.project.Config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ldw.squad.project.Entities.ClientModel;
import ldw.squad.project.Repository.ClientRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final ClientRepository clientRepository;

    public JwtAuthFilter(JwtUtil jwtUtil, ClientRepository clientRepository) {
        this.jwtUtil = jwtUtil;
        this.clientRepository = clientRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();

        // rotas publicas que nao demandam token
        if (path.equals("/auth/login") || path.equals("/home") || path.startsWith("/css/") 
            || path.startsWith("/js/") || path.equals("/clients")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendError(response, "Token ausente");
            return;
        }

        String jwt = authHeader.substring(7);
        String email;
        String role;

        try {
            email = jwtUtil.extractUsername(jwt);
            role = jwtUtil.extractRole(jwt);
        } catch (Exception e) {
            sendError(response, "Token inválido. A rota precisa de um token.");
            return;
        }

        if (!jwtUtil.validateToken(jwt)) {
            sendError(response, "Token inválido");
            return;
        }

        if (path.endsWith("/admin") && !"ADMIN".equals(role)) {
            sendForbidden(response, "Você não tem permissão para acessar esta rota");
            return;
        }

        Optional<ClientModel> clientOpt = clientRepository.findByEmail(email);

        if (clientOpt.isEmpty()) {
            sendError(response, "Usuário não encontrado");
            return;
        }

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(clientOpt.get(), null, null);
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }

    private void sendForbidden(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }


    private void sendError(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}
