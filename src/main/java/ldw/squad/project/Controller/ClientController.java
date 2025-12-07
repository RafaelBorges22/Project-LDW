package ldw.squad.project.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import ldw.squad.project.Dto.*;
import ldw.squad.project.Entities.ClientModel;
import ldw.squad.project.Mapper.ClientMapper;
import ldw.squad.project.Repository.ClientRepository;
import ldw.squad.project.Service.ClientService;
import ldw.squad.project.Service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/clients")
@Tag(name = "Clientes", description = "Gerenciamento de clientes e recuperação de senha")
public class ClientController {

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final ClientService clientService;

    @Operation(summary = "Listar todos os clientes", description = "Retorna uma lista com todos os clientes cadastrados.")
    @ApiResponse(responseCode = "200", description = "Clientes listados com sucesso.")
    @GetMapping
    public ResponseEntity<List<ClientDto>> getAllClients() {
        List<ClientDto> clients = clientService.getAllClients()
                .stream()
                .map(ClientMapper::toDto)
                .toList();

        return ResponseEntity.ok(clients);
    }

    @Operation(summary = "Buscar cliente por ID", description = "Retorna os dados de um cliente pelo seu UUID.")
    @ApiResponse(responseCode = "200", description = "Cliente encontrado.")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado.")
    @GetMapping("/{id}")
    public ResponseEntity<?> getClientById(@PathVariable UUID id) {
        Optional<ClientModel> clientOpt = clientRepository.findById(id);

        if (clientOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Cliente não encontrado");
        }

        return ResponseEntity.ok(ClientMapper.toDto(clientOpt.get()));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ClientDto> getClientByEmail(@PathVariable String email) {
        Optional<ClientModel> clientOpt = clientRepository.findByEmail(email);

        if (clientOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(ClientMapper.toDto(clientOpt.get()));
    }

    @Operation(summary = "Listar clientes com filtros opcionais", description = "Filtra clientes por nome ou email.")
    @GetMapping("/filter")
        public ResponseEntity<List<ClientDto>> getClientsFiltered(
                @RequestParam(required = false) String name,
                @RequestParam(required = false) String email) {

        List<ClientModel> clients = clientService.getAllClients();

        if (name != null && !name.isBlank()) {
            clients = clients.stream()
                    .filter(c -> c.getName().toLowerCase().contains(name.toLowerCase()))
                    .toList();
        }

        if (email != null && !email.isBlank()) {
            clients = clients.stream()
                    .filter(c -> c.getEmail().toLowerCase().contains(email.toLowerCase()))
                    .toList();
        }

        List<ClientDto> clientDtos = clients.stream()
                .map(ClientMapper::toDto)
                .toList();

        return ResponseEntity.ok(clientDtos);
    }

    @Operation(summary = "Atualizar cliente", description = "Atualiza os dados de um cliente existente.")
    @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso.")
    @PutMapping("/{id}")
    public ResponseEntity<String> updateClient(@PathVariable UUID id, @RequestBody UpdateClientDto dto) {

        Optional<ClientModel> clientOpt = clientRepository.findById(id);

        if (clientOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Cliente não encontrado");
        }

        try {
            ClientModel existingClient = clientOpt.get();

            String newPassword = dto.password();
            ClientMapper.updateEntity(existingClient, dto);

            if (newPassword != null && !newPassword.isEmpty()) {
                existingClient.setPassword(passwordEncoder.encode(newPassword));
            }

            clientService.update(existingClient);
            return ResponseEntity.ok("Cliente atualizado com sucesso!");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar cliente: " + e.getMessage());
        }
    }

    @Operation(summary = "Excluir cliente", description = "Remove um cliente pelo seu UUID.")
    @ApiResponse(responseCode = "204", description = "Cliente removido com sucesso.")
    @DeleteMapping("/{id}/admin")
    public ResponseEntity<String> deleteClient(@PathVariable UUID id) {

        boolean exists = clientRepository.existsById(id);
        if (!exists) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Cliente não encontrado. Nenhum registro foi excluído.");
        }

        clientService.delete(id);
        return ResponseEntity.ok("Usuário excluído com sucesso");
    }

    @Operation(summary = "Criar novo cliente", description = "Cria um novo cliente e envia e-mail de boas-vindas.")
    @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso.")
    @PostMapping
    public ResponseEntity<?> createClient(@RequestBody CreateClientDto dto) {

        Optional<ClientModel> existing = clientService.findByEmail(dto.email());
        if (existing.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Já existe um usuário cadastrado com esse e-mail.");
        }

        try {
            ClientModel client = ClientMapper.toEntity(dto);
            client.setPassword(passwordEncoder.encode(client.getPassword()));

            ClientModel newClient = clientService.save(client);

            String textBody =
                    "Olá " + safeName(newClient.getName()) + ",\n\n" +
                            "Parabéns! Seu cadastro foi realizado com sucesso na Família Kazu Tattoo.\n\n" +
                            "Estamos felizes em ter você conosco!\n\n" +
                            "Atenciosamente,\nEquipe do Estúdio";

            String htmlBody =
                    "<!doctype html><html><head><meta charset='utf-8'/>" +
                            "<meta name='viewport' content='width=device-width,initial-scale=1'/>" +
                            "<style>" +
                            "body{font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,Arial;margin:0;background:#f6f3f0}" +
                            ".wrap{max-width:680px;margin:20px auto;padding:0 16px}" +
                            ".card{background:#fff;border-radius:12px;overflow:hidden;box-shadow:0 10px 30px rgba(0,0,0,0.08)}" +
                            ".header{background:linear-gradient(180deg,#5C3A33,#70473f);padding:20px;color:#fff}" +
                            ".body{padding:26px;color:#222}" +
                            ".muted{color:#666;font-size:14px}.info{margin-top:12px;color:#444;font-size:14px;line-height:1.5}" +
                            ".footer{padding:16px;background:#fbf7f4;color:#777;font-size:13px}" +
                            "@media (max-width:480px){.body{padding:18px}.header{padding:16px}}" +
                            "</style></head><body>" +
                            "<div class='wrap'><div class='card'>" +
                            "<div class='header'><h2 style='margin:0;font-size:18px'>Bem-vindo à Família Kazu Tattoo!</h2></div>" +
                            "<div class='body'>" +
                            "<p>Olá <strong>" + escapeHtml(safeName(newClient.getName())) + "</strong>,</p>" +
                            "<p class='muted'>Seu cadastro foi realizado com sucesso! Estamos muito felizes em ter você como nosso cliente.</p>" +
                            "<div style='display:block;margin:18px auto;padding:18px 22px;background:#e9fceb;border:1px dashed #c0e0c5;border-radius:8px;text-align:center;color:#2c7d32;font-weight:bold;font-size:16px;'>CADASTRO CONFIRMADO COM SUCESSO!</div>" +
                            "<p class='info'>Você já pode realizar login na nossa plataforma e agendar sua próxima sessão!</p>" +
                            "<hr style='border:none;border-top:1px solid #eee;margin:20px 0'/>" +
                            "<p class='muted'>Se tiver qualquer dúvida, entre em contato conosco.</p>" +
                            "</div><div class='footer'>Atenciosamente,<br/>Equipe do Estúdio</div>" +
                            "</div></div></body></html>";


            try {
                emailService.enviarEmailHtml(newClient.getEmail(), "Bem-vindo à Família Kazu Tattoo", htmlBody, textBody);
            } catch (NoSuchMethodError | RuntimeException ex) {
                emailService.enviarEmailText(
                        newClient.getEmail(),
                        "Bem-vindo à Família Kazu Tattoo",
                        textBody
                );
            }

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("Usuário criado com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao criar usuário: " + e.getMessage());
        }
    }

    @Operation(summary = "Solicitar redefinição de senha", description = "Envia e-mail com token de redefinição caso o e-mail esteja cadastrado.")
    @PostMapping("/request")
    public ResponseEntity<String> requestPasswordReset(@RequestBody AceptPasswordDto dto) {

        Optional<ClientModel> clientOpt = clientService.findByEmail(dto.email());
        if (clientOpt.isEmpty()) {
            return ResponseEntity.ok("Se o e-mail estiver cadastrado, um código será enviado.");
        }

        ClientModel client = clientOpt.get();

        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(1);

        clientService.createResetToken(client, token, expiryDate);

        String textBody =
                "Olá " + safeName(client.getName()) + ",\n\n" +
                        "Recebemos uma solicitação para redefinir sua senha. Use o código abaixo no site para prosseguir com a redefinição:\n\n" +
                        token + "\n\n" +
                        "Este código expira em 1 hora.\n" +
                        "Se você não solicitou essa alteração, ignore este e-mail.";

        String htmlBody =
                "<!doctype html><html><head><meta charset='utf-8'/>" +
                        "<meta name='viewport' content='width=device-width,initial-scale=1'/>" +
                        "<style>" +
                        "body{font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,Arial;margin:0;background:#f6f3f0}" +
                        ".wrap{max-width:680px;margin:20px auto;padding:0 16px}" +
                        ".card{background:#fff;border-radius:12px;overflow:hidden;box-shadow:0 10px 30px rgba(0,0,0,0.08)}" +
                        ".header{background:linear-gradient(180deg,#5C3A33,#70473f);padding:20px;color:#fff}" +
                        ".body{padding:26px;color:#222}" +
                        ".codebox{display:block;margin:18px auto;padding:18px 22px;background:#faf5f2;border:1px dashed #e6d7d4;border-radius:8px;font-family:monospace;font-size:16px;letter-spacing:0.8px;text-align:center;color:#3a2a26;word-break:break-all}" +
                        ".muted{color:#666;font-size:14px}.info{margin-top:12px;color:#444;font-size:14px;line-height:1.5}" +
                        ".footer{padding:16px;background:#fbf7f4;color:#777;font-size:13px}" +
                        "@media (max-width:480px){.body{padding:18px}.header{padding:16px}.codebox{font-size:15px;padding:14px}}" +
                        "</style></head><body>" +
                        "<div class='wrap'><div class='card'>" +
                        "<div class='header'><h2 style='margin:0;font-size:18px'>Redefinição de senha</h2></div>" +
                        "<div class='body'>" +
                        "<p>Olá <strong>" + escapeHtml(safeName(client.getName())) + "</strong>,</p>" +
                        "<p class='muted'>Recebemos um pedido para redefinir a sua senha. Utilize o código abaixo dentro do formulário de redefinição no site (cole o código no campo apropriado).</p>" +
                        "<div class='codebox' aria-label='Código de redefinição'>" + escapeHtml(token) + "</div>" +
                        "<p class='info'>• O código expira em <strong>1 hora</strong>.<br/>" +
                        "• Não compartilhe este código com ninguém.<br/>" +
                        "• Se você não solicitou a redefinição, ignore este e-mail.</p>" +
                        "<hr style='border:none;border-top:1px solid #eee;margin:20px 0'/>" +
                        "<p class='muted'>Se tiver problemas ao colar o código, verifique espaços antes ou depois do texto ao colar. Caso precise de ajuda, responda este e-mail.</p>" +
                        "</div><div class='footer'>Atenciosamente,<br/>Equipe do Estúdio</div>" +
                        "</div></div></body></html>";

        try {
            emailService.enviarEmailHtml(client.getEmail(), "Código para redefinição de senha — Estúdio", htmlBody, textBody);
        } catch (NoSuchMethodError | RuntimeException ex) {
            // caso EmailService não tenha enviarEmailHtml, usa texto simples
            emailService.enviarEmailText(client.getEmail(), "Redefinição de Senha", textBody);
        }

        return ResponseEntity.ok("Se o e-mail estiver cadastrado, um código de redefinição será enviado.");
    }

    @Operation(summary = "Redefinir senha", description = "Redefine a senha do cliente com base no token recebido por e-mail.")
    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordDto dto) {

        ClientModel client;
        try {
            client = clientService.findByResetToken(dto.token());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        client.setPassword(passwordEncoder.encode(dto.newPassword()));
        client.setResetPasswordToken(null);
        client.setResetPasswordTokenExpiry(null);

        clientService.save(client);

        return ResponseEntity.ok("Senha redefinida com sucesso!");
    }

    private static String safeName(String name) {
        return (name == null || name.trim().isEmpty()) ? "usuário" : name.trim();
    }

    private static String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }
}