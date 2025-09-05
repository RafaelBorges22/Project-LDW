package ldw.squad.project.Controller;

import ldw.squad.project.Config.JwtUtil;
import ldw.squad.project.Entities.ClientModel;
import ldw.squad.project.Repository.ClientRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

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

        String token = jwtUtil.generateToken(clientOpt.get().getEmail(), clientOpt.get().getRole());

        response.setContentType("application/json");
        response.getWriter().write("{\"token\": \"" + token + "\"}");
    }
}
