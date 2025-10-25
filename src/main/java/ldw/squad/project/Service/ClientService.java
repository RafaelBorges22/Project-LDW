package ldw.squad.project.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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

    //Serviço de esqueceu senha
    public Optional<ClientModel> findByEmail(String email) {
        return clientRepository.findByEmail(email);
    }

    public ClientModel findByResetToken(String token) {
        return clientRepository.findByResetPasswordToken(token)
                .filter(client -> client.getResetPasswordTokenExpiry() != null)
                .filter(client -> client.getResetPasswordTokenExpiry().isAfter(LocalDateTime.now()))
                .orElseThrow(() -> new RuntimeException("Token inválido ou expirado."));
    }

    // Novo: Salva o token de redefinição no cliente
    public ClientModel createResetToken(ClientModel client, String token, LocalDateTime expiryDate) {
        client.setResetPasswordToken(token);
        client.setResetPasswordTokenExpiry(expiryDate);
        return clientRepository.save(client);
    }
}

