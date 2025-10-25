package ldw.squad.project.Controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import ldw.squad.project.Entities.ClientModel;
import ldw.squad.project.Entities.QuoteModel;
import ldw.squad.project.Repository.ClientRepository;
import ldw.squad.project.Repository.QuoteRepository;
import ldw.squad.project.Service.EmailService;
import ldw.squad.project.Service.QuoteService;
import ldw.squad.project.Service.UploadImageService;

@RestController
@RequestMapping("/quotes")
public class QuoteController {

    @Autowired
    private QuoteRepository quoteRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private QuoteService quoteService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UploadImageService imageService;

    // GET: retorna todos os quotes no banco
    @GetMapping
    public List<QuoteModel> getAllQuotes() {
        return quoteRepository.findAll();
    }

    // GET: retorna um quote específico pelo ID
    @GetMapping("/{id}")
    public ResponseEntity<QuoteModel> getQuoteById(@PathVariable Long id) {
        Optional<QuoteModel> quote = quoteRepository.findById(id);
        return quote.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    //Get Quote baseado no id do cliente (histórico de orçamentos)
    @GetMapping(path = "/{clientId}/history")
    public ResponseEntity<List<QuoteModel>> getQuotesByClient(@PathVariable UUID clientId) {
        Optional<ClientModel> clientOpt = clientRepository.findById(clientId);

        if (clientOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<QuoteModel> quotes = quoteRepository.findByClient(clientOpt.get());

        if (quotes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.ok(quotes);
    }
    @PostMapping(path = "/{clientId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<QuoteModel> createQuote(
            @PathVariable UUID clientId,
            @RequestPart("quote") QuoteModel quote,
            @RequestPart("image") MultipartFile image) throws IOException {

        // Busca o cliente pelo ID
        Optional<ClientModel> clientOptional = clientRepository.findById(clientId);

        //Se o cliente não for encontrado, retorna um erro 404 Not Found
        if (clientOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Associa o cliente ao quote
        ClientModel client = clientOptional.get();
        quote.setClient(client);

        // Salva a imagem e define a URL no quote
        String filename = imageService.saveFile(image);
        String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/upload/download/")
                .path(filename)
                .toUriString();
        quote.setImageUrl(imageUrl);


        // Calcula o valor final e salva o quote
        double finalValue = quoteService.calculateBasePrice(quote);
        quote.setFinalValue(finalValue);
        quote.setAdditionalCost(null);

        QuoteModel savedQuote = quoteRepository.save(quote);

        emailService.enviarEmailText(client.getEmail(),
                "Orçamento Enviado com sucesso!!",
                "Obrigado por confiar em nosso serviço, o tatuador entrará em contato com mais informações. "
        );

        return new ResponseEntity<>(savedQuote, HttpStatus.CREATED);
    }

    // PUT: atualiza apenas os dados da tatuagem, sem alterar preço ou adicional
    @PutMapping("/{id}")
    public ResponseEntity<QuoteModel> updateQuote(@PathVariable Long id, @RequestBody QuoteModel quoteDetails) {
        Optional<QuoteModel> optionalQuote = quoteRepository.findById(id);

        if (optionalQuote.isPresent()) {
            QuoteModel quote = optionalQuote.get();

            quote.setDescription(quoteDetails.getDescription());
            quote.setSize(quoteDetails.getSize());
            quote.setBodyPart(quoteDetails.getBodyPart());
            quote.setColored(quoteDetails.isColored());

            QuoteModel updatedQuote = quoteRepository.save(quote);
            return ResponseEntity.ok(updatedQuote);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE: remove um quote do banco
    @DeleteMapping("/{id}/admin")
    public ResponseEntity<Void> deleteQuote(@PathVariable Long id) {
        if (quoteRepository.existsById(id)) {
            quoteRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //A partir daqui, começam os endpoints especiais.

    // POST: adiciona um valor adicional ao quote e recalcula o valor final
    // Pode receber valores negativos para reduzir o preço
    @PostMapping("/{id}/additional/admin")
    public ResponseEntity<QuoteModel> addAdditionalCost(@PathVariable Long id, @RequestBody AdditionalCostRequest request) {
        Optional<QuoteModel> optionalQuote = quoteRepository.findById(id);

        if (optionalQuote.isPresent()) {
            QuoteModel quote = optionalQuote.get();
            quoteService.addAdditionalCost(quote, request.getAdditionalCost()); // recalcula a partir do preço base
            QuoteModel updatedQuote = quoteRepository.save(quote);
            return ResponseEntity.ok(updatedQuote);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // DTO para enviar apenas o valor adicional
    public static class AdditionalCostRequest {
        private Double additionalCost;

        public Double getAdditionalCost() { return additionalCost; }
        public void setAdditionalCost(Double additionalCost) { this.additionalCost = additionalCost; }
    }
}

//Link da Requisição caso consigam acessar:
// https://web.postman.co/workspace/Personal-Workspace~80d41166-1b43-4b0e-a9e0-315da934247e/collection/38183942-edb2bde7-3a05-4084-9ffc-bc71084753ef?action=share&source=copy-link&creator=38183942








