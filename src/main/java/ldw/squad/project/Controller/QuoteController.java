package ldw.squad.project.Controller;

import ldw.squad.project.Entities.QuoteModel;
import ldw.squad.project.Repository.QuoteRepository;
import ldw.squad.project.Service.QuoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/quotes")
public class QuoteController {

    @Autowired
    private QuoteRepository quoteRepository;

    @Autowired
    private QuoteService quoteService;

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

    // POST: cria um quote e calcula automaticamente o preço final com base nos parâmetros
    @PostMapping
    public ResponseEntity<QuoteModel> createQuote(@RequestBody QuoteModel quote) {
        double finalValue = quoteService.calculateBasePrice(quote); // preço calculado automaticamente
        quote.setFinalValue(finalValue);
        quote.setAdditionalCost(null); // ainda não há custo adicional

        QuoteModel savedQuote = quoteRepository.save(quote);
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
