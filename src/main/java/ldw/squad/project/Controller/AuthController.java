package ldw.squad.project.Controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import ldw.squad.project.Config.JwtUtil;
import ldw.squad.project.Entities.ClientModel;
import ldw.squad.project.Repository.ClientRepository;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(ClientRepository clientRepository,
                          PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil) {
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public void login(@RequestBody Map<String, String> user, HttpServletResponse response) throws IOException {
        String email = user.get("email");
        String password = user.get("password");

        Optional<ClientModel> clientOpt = clientRepository.findByEmail(email);

        if (clientOpt.isEmpty() || !passwordEncoder.matches(password, clientOpt.get().getPassword())) {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Email ou senha inválidos\"}");
            return;
        }

        ClientModel client = clientOpt.get();

        // mantém seu token atual
        String token = jwtUtil.generateToken(client.getEmail(), client.getRole());

        // agora devolve também o nome
        Map<String, Object> body = new HashMap<>();
        body.put("token", token);
        body.put("name", client.getName());

        response.setContentType("application/json");
        response.getWriter().write(
            "{\"token\":\"" + token + "\",\"name\":\"" + escapeJson(client.getName()) + "\"}"
        );
    }

    // utilitário simples para evitar quebrar JSON em nomes com aspas
    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
