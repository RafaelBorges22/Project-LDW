package ldw.squad.project.Service;

import java.util.Map;

import org.springframework.stereotype.Service;

import ldw.squad.project.Entities.QuoteModel;
import ldw.squad.project.Entities.Enums.BodyPart;
import ldw.squad.project.Entities.Enums.Size;

@Service
public class QuoteService {
	
	// Os valores aqui serão somados à variável "valor final" dependendo da escolha do cliente:
    private static final Map<Size, Double> priceBySize = Map.of(
        Size.SMALL, 80.0,
        Size.MEDIUM, 150.0,
        Size.LARGE, 200.0
    );
    
    // Aqui também:
    private static final Map<BodyPart, Double> extraByBodyPart = Map.of(
        BodyPart.ARM, 0.0,
        BodyPart.BACK, 0.0,
        BodyPart.LEG, 0.0,
        BodyPart.CHEST, 0.0,
        BodyPart.RIB, 50.0,
        BodyPart.NECK, 50.0,
        BodyPart.HAND, 50.0,
        BodyPart.HEAD, 50.0,
        BodyPart.FOOT, 50.0,
        BodyPart.OTHER, 80.0
    );
    
    // O valor abaixo representa o número que será multiplicado pelo valor final caso a tatuagem seja colorida.
    private static final double COLOR_MULTIPLIER = 1.33;

    public double calculateBasePrice(QuoteModel quote) {
        double price = priceBySize.getOrDefault(quote.getSize(), 0.0);
        price += extraByBodyPart.getOrDefault(quote.getBodyPart(), 0.0);

        if (quote.isColored()) {
            price *= COLOR_MULTIPLIER;
        }

        return price;
    }
    
    // Essa função roda ao atualizar o orçamento e insere o valor adicional, caso ele tenha sido modificado.
    public double addAdditionalCost(QuoteModel quote, Double additionalCost) {
        if (additionalCost != null) {
            double currentFinal = (quote.getFinalValue() == null ? 0 : quote.getFinalValue());
            quote.setFinalValue(currentFinal + additionalCost);
            quote.setAdditionalCost(additionalCost);
        }
        return quote.getFinalValue();
    }
}
