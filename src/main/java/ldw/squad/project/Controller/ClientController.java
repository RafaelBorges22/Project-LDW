package ldw.squad.project.Controller;

import ldw.squad.project.Entities.ClientModel;
import ldw.squad.project.Repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.List;

@RestController
@RequestMapping("/clients")
public class ClientController {
    @Autowired
    private ClientRepository clienteRepository;

    @GetMapping("/clients")
    public List<ClientModel> getAllClients(){
        return clienteRepository.findAll();
    }

    @PostMapping("/clients")
    public ResponseEntity<ClientModel> createClient(@RequestBody ClientModel client){
        ClientModel newClient = clienteRepository.save(client);
        return new ResponseEntity<>(newClient, HttpStatus.CREATED);
    }

}
