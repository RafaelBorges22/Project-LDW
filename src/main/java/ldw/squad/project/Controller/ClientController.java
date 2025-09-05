package ldw.squad.project.Controller;

import java.util.List;
import java.util.Optional;

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
import ldw.squad.project.Repository.ClientRepository;

@RestController
@RequestMapping("/clients")
public class ClientController {

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @GetMapping
    public List<ClientModel> getAllClients() {
        return clientRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientModel> getClientById(@PathVariable Long id) {
        Optional<ClientModel> client = clientRepository.findById(id);
        return client.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ClientModel> createClient(@RequestBody ClientModel client) {
        client.setPassword(passwordEncoder.encode(client.getPassword()));
        ClientModel newClient = clientRepository.save(client);
        return new ResponseEntity<>(newClient, HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    public ResponseEntity<ClientModel> updateClient(@PathVariable Long id, @RequestBody ClientModel clientDetails) {
        Optional<ClientModel> optionalClient = clientRepository.findById(id);

        if (optionalClient.isPresent()) {
            ClientModel existingClient = optionalClient.get();
            existingClient.setName(clientDetails.getName());
            existingClient.setEmail(clientDetails.getEmail());
            ClientModel updatedClient = clientRepository.save(existingClient);
            return ResponseEntity.ok(updatedClient);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}/admin")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        if (clientRepository.existsById(id)) {
            clientRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
