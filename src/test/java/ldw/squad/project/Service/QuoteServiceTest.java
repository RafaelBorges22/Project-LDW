package ldw.squad.project.Service;

import ldw.squad.project.Entities.Enums.BodyPart;
import ldw.squad.project.Entities.Enums.Size;
import ldw.squad.project.Entities.QuoteModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QuoteServiceTest {

    private QuoteService quoteService;

    @BeforeEach
    void setUp() {
        quoteService = new QuoteService();
    }

    @Test
    @DisplayName("Should calculate price for a small, simple tattoo")
    void testCalculateBasePrice_SmallSimpleTattoo() {
        // Arrange
        QuoteModel quote = new QuoteModel();
        quote.setSize(Size.SMALL);
        quote.setBodyPart(BodyPart.ARM);
        quote.setColored(false);

        // Act
        double price = quoteService.calculateBasePrice(quote);

        // Assert
        assertEquals(80.0, price, 0.01);
    }

    @Test
    @DisplayName("Should calculate price for a medium, colored tattoo on ribs")
    void testCalculateBasePrice_MediumColoredRibTattoo() {
        // Arrange
        QuoteModel quote = new QuoteModel();
        quote.setSize(Size.MEDIUM);
        quote.setBodyPart(BodyPart.RIB);
        quote.setColored(true);

        // Act
        double price = quoteService.calculateBasePrice(quote);

        // Assert
        assertEquals(266.0, price, 0.01);
    }

    @Test
    @DisplayName("Should calculate price for a large tattoo on foot")
    void testCalculateBasePrice_LargeFootTattoo() {
        // Arrange
        QuoteModel quote = new QuoteModel();
        quote.setSize(Size.LARGE);
        quote.setBodyPart(BodyPart.FOOT);
        quote.setColored(false);

        // Act
        double price = quoteService.calculateBasePrice(quote);

        // Assert
        assertEquals(250.0, price, 0.01);
    }
    
    @Test
    @DisplayName("Should add additional cost to a quote with no initial final value")
    void testAddAdditionalCost_NoInitialValue() {
        // Arrange
        QuoteModel quote = new QuoteModel();
        Double additionalCost = 100.0;

        // Act
        double finalValue = quoteService.addAdditionalCost(quote, additionalCost);

        // Assert
        assertEquals(100.0, finalValue, 0.01);
        assertEquals(100.0, quote.getAdditionalCost(), 0.01);
    }

    @Test
    @DisplayName("Should add additional cost to a quote with an existing final value")
    void testAddAdditionalCost_WithInitialValue() {
        // Arrange
        QuoteModel quote = new QuoteModel();
        quote.setFinalValue(250.0);
        Double additionalCost = 50.0;

        // Act
        double finalValue = quoteService.addAdditionalCost(quote, additionalCost);

        // Assert
        assertEquals(300.0, finalValue, 0.01);
        assertEquals(50.0, quote.getAdditionalCost(), 0.01);
    }

    @Test
    @DisplayName("Should not change final value if additional cost is null")
    void testAddAdditionalCost_NullAdditionalCost() {
        // Arrange
        QuoteModel quote = new QuoteModel();
        quote.setFinalValue(250.0);
        Double additionalCost = null;

        // Act
        double finalValue = quoteService.addAdditionalCost(quote, additionalCost);

        // Assert
        assertEquals(250.0, finalValue, 0.01);
    }
}
