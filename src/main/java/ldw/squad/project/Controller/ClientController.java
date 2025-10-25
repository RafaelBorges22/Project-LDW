package ldw.squad.project.Controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.annotation.PostConstruct;
import ldw.squad.project.Dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ldw.squad.project.Entities.ClientModel;
import ldw.squad.project.Mapper.ClientMapper;
import ldw.squad.project.Repository.ClientRepository;
import ldw.squad.project.Service.ClientService;
import ldw.squad.project.Service.EmailService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/clients")
public class ClientController {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

	@Autowired
	private EmailService emailService;

    @Autowired
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
    public ResponseEntity<ClientDto> createClient(@RequestBody CreateClientDto dto) {
        ClientModel client = ClientMapper.toEntity(dto);
        client.setPassword(passwordEncoder.encode(client.getPassword()));

        ClientModel newClient = clientService.save(client);

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
    public ResponseEntity<Void> deleteClient(@PathVariable UUID  id) {
        clientService.delete(id);
        return ResponseEntity.noContent().build();
    }


    //Esqueceu senha
    @PostMapping("/request")
    public ResponseEntity<String> requestPasswordReset(@RequestBody AceptPasswordDto dto) {
        Optional<ClientModel> clientOpt = clientService.findByEmail(dto.email());

        if (clientOpt.isEmpty()) {
            return ResponseEntity.ok("Se o e-mail estiver cadastrado, um link de redefinição será enviado.");
        }

        ClientModel client = clientOpt.get();

        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(1);
        clientService.createResetToken(client, token, expiryDate);

        String resetUrl = "http://frontend/reset-password?token=" + token;
        String emailBody = "Olá " + client.getName() + ", ... link: " + resetUrl
                + "Você solicitou uma redefinição de senha. Use o link abaixo:\\n"
                + resetUrl + "\\n\\n"
                + "Este link expira em 1 hora.\\n"
                + "Se você não solicitou isso, ignore este e-mail.";

        System.out.println("DEBUG: Prestes a enviar e-mail para: " + client.getEmail());
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

