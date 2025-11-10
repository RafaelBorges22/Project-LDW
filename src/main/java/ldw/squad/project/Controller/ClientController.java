package ldw.squad.project.Controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import ldw.squad.project.Dto.*;
import ldw.squad.project.Entities.ClientModel;
import ldw.squad.project.Mapper.ClientMapper;
import ldw.squad.project.Repository.ClientRepository;
import ldw.squad.project.Service.ClientService;
import ldw.squad.project.Service.EmailService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/clients")
@Tag(name = "Clientes", description = "Gerenciamento de clientes e recuperação de senha")
public class ClientController {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
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
    public ResponseEntity<ClientDto> getClientById(@PathVariable UUID id) {
        ClientModel client = clientService.getClientById(id);
        return ResponseEntity.ok(ClientMapper.toDto(client));
    }

    @Operation(summary = "Criar novo cliente", description = "Cria um novo cliente e envia e-mail de boas-vindas.")
    @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso.")
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

    @Operation(summary = "Atualizar cliente", description = "Atualiza os dados de um cliente existente.")
    @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso.")
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

    @Operation(summary = "Excluir cliente (admin)", description = "Remove um cliente pelo seu UUID.")
    @ApiResponse(responseCode = "204", description = "Cliente removido com sucesso.")
    @DeleteMapping("/{id}/admin")
    public ResponseEntity<Void> deleteClient(@PathVariable UUID id) {
        clientService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Solicitar redefinição de senha", description = "Envia e-mail com link de redefinição caso o e-mail esteja cadastrado.")
    @PostMapping("/request")
    public ResponseEntity<String> requestPasswordReset(@RequestBody AceptPasswordDto dto) {
        Optional<ClientModel> clientOpt = clientService.findByEmail(dto.email());

        if (clientOpt.isEmpty()) {
            return ResponseEntity.ok("Se o e-mail estiver cadastrado, um link será enviado.");
        }

        ClientModel client = clientOpt.get();
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(1);
        clientService.createResetToken(client, token, expiryDate);

        String resetUrl = "http://frontend/reset-password?token=" + token;
        String emailBody = "Olá " + client.getName() + ",\nUse o link: " + resetUrl;

        emailService.enviarEmailText(client.getEmail(), "Redefinição de Senha", emailBody);
        return ResponseEntity.ok("Se o e-mail estiver cadastrado, um link de redefinição será enviado.");
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

        String newEncodedPassword = passwordEncoder.encode(dto.newPassword());
        client.setPassword(newEncodedPassword);
        client.setResetPasswordToken(null);
        client.setResetPasswordTokenExpiry(null);
        clientService.save(client);

        return ResponseEntity.ok("Senha redefinida com sucesso!");
    }
}
