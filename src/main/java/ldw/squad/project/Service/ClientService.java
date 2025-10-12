package ldw.squad.project.Service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import ldw.squad.project.Entities.ClientModel;
import ldw.squad.project.Repository.ClientRepository;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public List<ClientModel> getAllClients() {
        return clientRepository.findAll();
    }

    public ClientModel getClientById(UUID id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com id: " + id));
    }

    public ClientModel save(ClientModel client) {
        return clientRepository.save(client);
    }

    public ClientModel update(ClientModel client) {
        return clientRepository.save(client);
    }

    public void delete(UUID id) {
        clientRepository.deleteById(id);
    }
}

