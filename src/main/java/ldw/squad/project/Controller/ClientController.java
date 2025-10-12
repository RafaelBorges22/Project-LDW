package ldw.squad.project.Controller;

import java.util.List;
import java.util.UUID;

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

import ldw.squad.project.Dto.ClientDto;
import ldw.squad.project.Dto.CreateClientDto;
import ldw.squad.project.Dto.UpdateClientDto;
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
        ClientMapper.updateEntity(existingClient, dto);

        ClientModel updatedClient = clientService.update(existingClient);
        return ResponseEntity.ok(ClientMapper.toDto(updatedClient));
    }

    @DeleteMapping("/{id}/admin")
    public ResponseEntity<Void> deleteClient(@PathVariable UUID  id) {
        clientService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
