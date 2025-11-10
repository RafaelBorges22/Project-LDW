package ldw.squad.project.Controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

@RequiredArgsConstructor
@RestController
@RequestMapping("/clients")
public class ClientController {

    // usando lombok para injeção por construtor
    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final ClientService clientService;

    @GetMapping
    public ResponseEntity<List<ClientDto>> getAllClients() {
        List<ClientDto> clients = clientService.getAllClients()
                .stream()
                .map(ClientMapper::toDto)
                .toList();
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientDto> getClientById(@PathVariable UUID id) {
        ClientModel client = clientService.getClientById(id);
        return ResponseEntity.ok(ClientMapper.toDto(client));
    }

    @PostMapping
    public ResponseEntity<?> createClient(@RequestBody CreateClientDto dto) {
        // 1) valida se já existe esse e-mail
        Optional<ClientModel> existing = clientService.findByEmail(dto.email());
        if (existing.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Já existe um usuário cadastrado com esse e-mail.");
        }

        // 2) cria normalmente
        ClientModel client = ClientMapper.toEntity(dto);
        client.setPassword(passwordEncoder.encode(client.getPassword()));

        ClientModel newClient = clientService.save(client);

        // 3) envia e-mail
        emailService.enviarEmailText(
                newClient.getEmail(),
                "Bem-vindo à Família Kazu Tattoo",
                "Parabéns! Seu cadastro foi realizado com sucesso!"
        );

        return new ResponseEntity<>(ClientMapper.toDto(newClient), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientDto> updateClient(@PathVariable UUID id, @RequestBody UpdateClientDto dto) {
        ClientModel existingClient = clientService.getClientById(id);
        String newPassword = dto.password();
        ClientMapper.updateEntity(existingClient, dto);

        if (newPassword != null && !newPassword.isEmpty()) {
            existingClient.setPassword(passwordEncoder.encode(newPassword));
        }

        ClientModel updatedClient = clientService.update(existingClient);
        return ResponseEntity.ok(ClientMapper.toDto(updatedClient));
    }

    @DeleteMapping("/{id}/admin")
    public ResponseEntity<Void> deleteClient(@PathVariable UUID id) {
        clientService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Esqueceu senha
    @PostMapping("/request")
    public ResponseEntity<String> requestPasswordReset(@RequestBody AceptPasswordDto dto) {
        Optional<ClientModel> clientOpt = clientService.findByEmail(dto.email());

        // mesmo se não achar, responde igual (boa prática de segurança)
        if (clientOpt.isEmpty()) {
            return ResponseEntity.ok("Se o e-mail estiver cadastrado, um link de redefinição será enviado.");
        }

        ClientModel client = clientOpt.get();

        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(1);
        clientService.createResetToken(client, token, expiryDate);

        String resetUrl = "http://frontend/reset-password?token=" + token;
        String emailBody =
                "Olá " + client.getName() + ",\n\n" +
                "Você solicitou uma redefinição de senha. Use o link abaixo:\n" +
                resetUrl + "\n\n" +
                "Este link expira em 1 hora.\n" +
                "Se você não solicitou isso, ignore este e-mail.";

        emailService.enviarEmailText(client.getEmail(), "Redefinição de Senha", emailBody);

        return ResponseEntity.ok("Se o e-mail estiver cadastrado, um link de redefinição será enviado.");
    }

    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordDto dto) {

        ClientModel client;
        try {
            client = clientService.findByResetToken(dto.token());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        String newEncodedPassword = passwordEncoder.encode(dto.newPassword());
        client.setPassword(newEncodedPassword);

        client.setResetPasswordToken(null);
        client.setResetPasswordTokenExpiry(null);

        clientService.save(client);

        return ResponseEntity.ok("Senha redefinida com sucesso! Você já pode fazer login.");
    }
}
