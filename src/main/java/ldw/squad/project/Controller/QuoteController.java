package ldw.squad.project.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import ldw.squad.project.Dto.ClientDto;
import ldw.squad.project.Dto.UpdateQuoteDto;
import ldw.squad.project.Mapper.ClientMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import ldw.squad.project.Dto.QuoteDto;
import ldw.squad.project.Dto.CreateQuoteDto;
import ldw.squad.project.Entities.ClientModel;
import ldw.squad.project.Entities.QuoteModel;
import ldw.squad.project.Mapper.QuoteMapper;
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


    @Operation(summary = "Listar todos os orçamentos")
    @GetMapping
    public List<QuoteDto> getAllQuotes() {
        return quoteRepository.findAll()
                .stream()
                .map(QuoteMapper::toDto)
                .toList();
    }

    @Operation(summary = "Buscar orçamento por ID")
    @GetMapping("/{id}")
    public ResponseEntity<QuoteDto> getQuoteById(@PathVariable Long id) {
        return quoteRepository.findById(id)
                .map(QuoteMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @Operation(summary = "Listar histórico de orçamentos de um cliente")
    @GetMapping("/{clientId}/history")
    public ResponseEntity<List<QuoteDto>> getQuotesByClient(@PathVariable UUID clientId) {
        Optional<ClientModel> clientOpt = clientRepository.findById(clientId);
        if (clientOpt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        List<QuoteModel> quotes = quoteRepository.findByClient(clientOpt.get());

        if (quotes.isEmpty()) return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

        return ResponseEntity.ok(
                quotes.stream().map(QuoteMapper::toDto).toList()
        );
    }


    @Operation(summary = "Criar novo orçamento")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<QuoteDto> createQuote(
            @RequestPart("quote") CreateQuoteDto dto,
            @RequestPart("image") MultipartFile image
    ) throws IOException {

        Optional<ClientModel> clientOpt = clientRepository.findById(dto.getClientId());
        if (clientOpt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        ClientModel client = clientOpt.get();
        QuoteModel quote = QuoteMapper.toEntity(dto);
        quote.setClient(client);

        // Upload de imagem
        String filename = imageService.saveFile(image);
        String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/upload/download/")
                .path(filename)
                .toUriString();
        quote.setImageUrl(imageUrl);

        // Calcula valor
        double finalValue = quoteService.calculateBasePrice(quote);
        quote.setFinalValue(finalValue);

        QuoteModel savedQuote = quoteRepository.save(quote);

        // Enviar email
        emailService.enviarEmailText(
                client.getEmail(),
                "Orçamento Enviado com Sucesso!",
                "Obrigado por confiar em nosso serviço, o tatuador entrará em contato."
        );

        return new ResponseEntity<>(QuoteMapper.toDto(savedQuote), HttpStatus.CREATED);
    }


    @Operation(summary = "Atualizar orçamento")
    @PutMapping("/{id}")
    public ResponseEntity<QuoteDto> updateQuote(
            @PathVariable Long id,
            @RequestBody UpdateQuoteDto dto
    ) {
        Optional<QuoteModel> optionalQuote = quoteRepository.findById(id);
        if (optionalQuote.isEmpty()) return ResponseEntity.notFound().build();
        QuoteModel quote = optionalQuote.get();

        if (dto.getFinalValue() != null) {
            quote.setFinalValue(dto.getFinalValue());
        }

        if (dto.getAdditionalCost() != null) {
            quote.setAdditionalCost(dto.getAdditionalCost());
        }

        if (dto.getState() != null) {
            quote.setState(dto.getState());
        }
        QuoteModel updatedQuote = quoteRepository.save(quote);

        return ResponseEntity.ok(QuoteMapper.toDto(updatedQuote));
    }

    @Operation(summary = "Excluir orçamento (admin)")
    @DeleteMapping("/{id}/admin")
    public ResponseEntity<Void> deleteQuote(@PathVariable Long id) {
        if (!quoteRepository.existsById(id))
            return ResponseEntity.notFound().build();

        quoteRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}


/* Curl para criação de Quote:

curl -X POST "http://localhost:8081/quotes" \
  -H "Accept: application/json" \
  -F 'quote={
    "clientId":"4703c6eb-a0de-4409-a2da-b3fcab59d556",
    "colored": false,
    "description": "Tatuagem realista",
    "size": "MEDIUM",
    "bodyPart": "ARM",
    "state": "WAITING"
  };type=application/json' \
  -F "image=@\"/C:/Users/Rafael Borges/Pictures/Saved Pictures/1_34DCJ1mW9TL2ztHgORlCrA.jpeg\""

 */



