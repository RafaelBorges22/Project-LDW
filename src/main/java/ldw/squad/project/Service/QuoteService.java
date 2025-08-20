package ldw.squad.project.Service;

import ldw.squad.project.Entities.QuoteModel;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class QuoteService {

    // Os valores aqui serão somados à variável "valor final" dependendo da escolha do cliente:
    private static final Map<QuoteModel.Size, Double> priceBySize = Map.of(
        QuoteModel.Size.SMALL, 80.0,
        QuoteModel.Size.MEDIUM, 150.0,
        QuoteModel.Size.LARGE, 200.0
    );
    
    // Aqui também:
    private static final Map<QuoteModel.BodyPart, Double> extraByBodyPart = Map.of(
        QuoteModel.BodyPart.ARM, 0.0,
        QuoteModel.BodyPart.BACK, 0.0,
        QuoteModel.BodyPart.LEG, 0.0,
        QuoteModel.BodyPart.CHEST, 0.0,
        QuoteModel.BodyPart.RIB, 50.0,
        QuoteModel.BodyPart.NECK, 50.0,
        QuoteModel.BodyPart.HAND, 50.0,
        QuoteModel.BodyPart.HEAD, 50.0,
        QuoteModel.BodyPart.FOOT, 50.0,
        QuoteModel.BodyPart.OTHER, 80.0
    );
    
	// O valor abaixo representa o número que será multiplicado pelo "valor final" caso a tatuagem seja colorida.
    // Atualmente, o aumento por cor é de 33%, a diferença que o cliente nos mostrou na mensagem (entre 150 e 200).
    private static final double COLOR_MULTIPLIER = 1.33; 

    // A função abaixo roda ao criar um orçamento:
    public double calculateBasePrice(QuoteModel quote) {
        double price = priceBySize.getOrDefault(quote.getSize(), 0.0); // pega o primeiro valor pelo tamanho;
        price += extraByBodyPart.getOrDefault(quote.getBodyPart(), 0.0); // soma a parte do corpo;

        if (quote.isColored()) {
            price *= COLOR_MULTIPLIER; // multiplica por 1.33 se for colorido;
        }

        return price; // retorna o valor final.
    }

    // Essa função roda ao atualizar o orçamento e insere o valor adicional, caso ele tenha sido modificado.
    public double addAdditionalCost(QuoteModel quote, Double additionalCost) {
        if (additionalCost != null) {
            quote.setFinalValue((quote.getFinalValue() == null ? 0 : quote.getFinalValue()) + additionalCost);
            quote.setAdditionalCost(additionalCost);
        }
        return quote.getFinalValue();
    }
}
