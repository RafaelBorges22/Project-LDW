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
    public ResponseEntity<?> getAllQuotes() {
        List<QuoteModel> quotes = quoteRepository.findAll();
        if (quotes.isEmpty()) {
            // 204 não permite corpo → usa 200 para exibir mensagem
            return ResponseEntity.status(HttpStatus.OK)
                    .body("Nenhum orçamento encontrado.");
        }
        return ResponseEntity.ok(quotes);
    }

    // GET: retorna um quote específico pelo ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getQuoteById(@PathVariable Long id) {
        Optional<QuoteModel> quote = quoteRepository.findById(id);
        if (quote.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Orçamento não encontrado.");
        }
        return ResponseEntity.ok(quote.get());
    }

    // GET: retorna histórico de orçamentos de um cliente
    @GetMapping("/{clientId}/history")
    public ResponseEntity<?> getQuotesByClient(@PathVariable UUID clientId) {
        Optional<ClientModel> clientOpt = clientRepository.findById(clientId);
        if (clientOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Cliente não encontrado.");
        }

        List<QuoteModel> quotes = quoteRepository.findByClient(clientOpt.get());
        if (quotes.isEmpty()) {
            // 204 não mostra corpo → substituído por 200
            return ResponseEntity.status(HttpStatus.OK)
                    .body("Nenhum orçamento encontrado para este cliente.");
        }

        return ResponseEntity.ok(quotes);
    }

    // POST: cria um novo orçamento (com imagem)
    @PostMapping(path = "/{clientId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> createQuote(
            @PathVariable UUID clientId,
            @RequestPart("quote") QuoteModel quote,
            @RequestPart("image") MultipartFile image) {
        try {
            Optional<ClientModel> clientOptional = clientRepository.findById(clientId);
            if (clientOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Cliente não encontrado. Não foi possível criar o orçamento.");
            }

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

            quoteRepository.save(quote);

            emailService.enviarEmailText(
                    client.getEmail(),
                    "Orçamento Enviado com Sucesso!",
                    "Obrigado por confiar em nosso serviço. O tatuador entrará em contato com mais informações."
            );

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Orçamento criado com sucesso!");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao processar imagem: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao criar orçamento: " + e.getMessage());
        }
    }

    // PUT: atualiza dados básicos do orçamento
    @PutMapping("/{id}")
    public ResponseEntity<String> updateQuote(@PathVariable Long id, @RequestBody QuoteModel quoteDetails) {
        Optional<QuoteModel> optionalQuote = quoteRepository.findById(id);

        if (optionalQuote.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Orçamento não encontrado.");
        }

        try {
            QuoteModel quote = optionalQuote.get();
            quote.setDescription(quoteDetails.getDescription());
            quote.setSize(quoteDetails.getSize());
            quote.setBodyPart(quoteDetails.getBodyPart());
            quote.setColored(quoteDetails.isColored());

            quoteRepository.save(quote);
            return ResponseEntity.ok("Orçamento atualizado com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar orçamento: " + e.getMessage());
        }
    }

    // DELETE: remove um orçamento (admin)
    @DeleteMapping("/{id}/admin")
    public ResponseEntity<String> deleteQuote(@PathVariable Long id) {
        if (!quoteRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Orçamento não encontrado. Nenhum registro foi excluído.");
        }

        quoteRepository.deleteById(id);
        return ResponseEntity.ok("Orçamento excluído com sucesso!");
    }

    // POST: adiciona custo adicional e recalcula valor final (admin)
    @PostMapping("/{id}/additional/admin")
    public ResponseEntity<String> addAdditionalCost(
            @PathVariable Long id,
            @RequestBody AdditionalCostRequest request) {
        Optional<QuoteModel> optionalQuote = quoteRepository.findById(id);

        if (optionalQuote.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Orçamento não encontrado.");
        }

        try {
            QuoteModel quote = optionalQuote.get();
            quoteService.addAdditionalCost(quote, request.getAdditionalCost());
            quoteRepository.save(quote);
            return ResponseEntity.ok("Custo adicional aplicado com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao adicionar custo adicional: " + e.getMessage());
        }
    }

    // DTO interno para envio de custo adicional
    public static class AdditionalCostRequest {
        private Double additionalCost;

        public Double getAdditionalCost() {
            return additionalCost;
        }

        public void setAdditionalCost(Double additionalCost) {
            this.additionalCost = additionalCost;
        }
    }
}
