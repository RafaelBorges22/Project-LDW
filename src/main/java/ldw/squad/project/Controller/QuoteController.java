package ldw.squad.project.Controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
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
@Tag(name = "Orçamentos (Quotes)", description = "Gerenciamento de orçamentos e envio de imagens")
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

    @Operation(summary = "Listar todos os orçamentos", description = "Retorna todos os orçamentos cadastrados.")
    @GetMapping
    public List<QuoteModel> getAllQuotes() {
        return quoteRepository.findAll();
    }

    @Operation(summary = "Buscar orçamento por ID")
    @GetMapping("/{id}")
    public ResponseEntity<QuoteModel> getQuoteById(@PathVariable Long id) {
        Optional<QuoteModel> quote = quoteRepository.findById(id);
        return quote.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Listar histórico de orçamentos de um cliente")
    @GetMapping("/{clientId}/history")
    public ResponseEntity<List<QuoteModel>> getQuotesByClient(@PathVariable UUID clientId) {
        Optional<ClientModel> clientOpt = clientRepository.findById(clientId);

        if (clientOpt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        List<QuoteModel> quotes = quoteRepository.findByClient(clientOpt.get());
        if (quotes.isEmpty()) return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

        return ResponseEntity.ok(quotes);
    }

    @Operation(summary = "Criar novo orçamento (com imagem)", description = "Cria um orçamento associado a um cliente e envia e-mail de confirmação.")
    @PostMapping(path = "/{clientId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<QuoteModel> createQuote(
            @PathVariable UUID clientId,
            @RequestPart("quote") QuoteModel quote,
            @RequestPart("image") MultipartFile image) throws IOException {

        Optional<ClientModel> clientOptional = clientRepository.findById(clientId);
        if (clientOptional.isEmpty()) return ResponseEntity.notFound().build();

        ClientModel client = clientOptional.get();
        quote.setClient(client);

        String filename = imageService.saveFile(image);
        String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/upload/download/")
                .path(filename)
                .toUriString();
        quote.setImageUrl(imageUrl);

        double finalValue = quoteService.calculateBasePrice(quote);
        quote.setFinalValue(finalValue);
        quote.setAdditionalCost(null);

        QuoteModel savedQuote = quoteRepository.save(quote);

        emailService.enviarEmailText(client.getEmail(),
                "Orçamento Enviado com Sucesso!",
                "Obrigado por confiar em nosso serviço, o tatuador entrará em contato.");

        return new ResponseEntity<>(savedQuote, HttpStatus.CREATED);
    }

    @Operation(summary = "Atualizar informações do orçamento")
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

    @Operation(summary = "Excluir orçamento (admin)")
    @DeleteMapping("/{id}/admin")
    public ResponseEntity<Void> deleteQuote(@PathVariable Long id) {
        if (quoteRepository.existsById(id)) {
            quoteRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Adicionar custo adicional (admin)")
    @PostMapping("/{id}/additional/admin")
    public ResponseEntity<QuoteModel> addAdditionalCost(@PathVariable Long id, @RequestBody AdditionalCostRequest request) {
        Optional<QuoteModel> optionalQuote = quoteRepository.findById(id);

        if (optionalQuote.isPresent()) {
            QuoteModel quote = optionalQuote.get();
            quoteService.addAdditionalCost(quote, request.getAdditionalCost());
            QuoteModel updatedQuote = quoteRepository.save(quote);
            return ResponseEntity.ok(updatedQuote);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public static class AdditionalCostRequest {
        private Double additionalCost;
        public Double getAdditionalCost() { return additionalCost; }
        public void setAdditionalCost(Double additionalCost) { this.additionalCost = additionalCost; }
    }
}


//Link da Requisição caso consigam acessar:
// https://web.postman.co/workspace/Personal-Workspace~80d41166-1b43-4b0e-a9e0-315da934247e/collection/38183942-edb2bde7-3a05-4084-9ffc-bc71084753ef?action=share&source=copy-link&creator=38183942








