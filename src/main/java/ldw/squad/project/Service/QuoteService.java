package ldw.squad.project.Service;

import java.util.Map;

import org.springframework.stereotype.Service;

import ldw.squad.project.Entities.QuoteModel;
import ldw.squad.project.Entities.Enums.BodyPart;
import ldw.squad.project.Entities.Enums.Size;

@Service
public class QuoteService {

    private static final Map<Size, Double> priceBySize = Map.of(
            Size.SMALL, 80.0,
            Size.MEDIUM, 150.0,
            Size.LARGE, 200.0
    );

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

    private static final double COLOR_MULTIPLIER = 1.33;

    /**
     * Calcula o valor estimado baseado no tamanho, parte do corpo e cor.
     */
    public double calculateBasePrice(QuoteModel quote) {
        double price = priceBySize.getOrDefault(quote.getSize(), 0.0);
        price += extraByBodyPart.getOrDefault(quote.getBodyPart(), 0.0);

        if (quote.isColored()) {
            price *= COLOR_MULTIPLIER;
        }

        return price;
    }

    public void updateQuoteValues(QuoteModel quote) {

        // 1. Calcular o valor estimado (base price)
        double estimated = calculateBasePrice(quote);
        quote.setEstimatedValue(estimated);

        // 2. Pegar o ajuste do tatuador (se houver)
        Double additional = quote.getAdditionalCost() != null
                ? quote.getAdditionalCost()
                : 0.0;

        // 3. Calcular o valor final
        quote.setFinalValue(estimated + additional);
    }
}
