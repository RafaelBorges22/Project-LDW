package ldw.squad.project.Mapper;

import ldw.squad.project.Dto.CreateQuoteDto;
import ldw.squad.project.Dto.QuoteDto;
import ldw.squad.project.Entities.QuoteModel;

public class QuoteMapper {

    public static QuoteDto toDto(QuoteModel quote) {
        return new QuoteDto(
                quote.getId(),
                quote.getDescription(),
                quote.getImageUrl(),
                quote.getSize(),
                quote.getBodyPart(),
                quote.getState(),
                quote.getFinalValue(),
                quote.isColored(),
                quote.getAdditionalCost(),
                quote.getClient().getId(),
                quote.getClient().getName(),
                quote.getClient().getEmail()
        );
    }

    public static QuoteModel toEntity(CreateQuoteDto dto) {
        QuoteModel quote = new QuoteModel();
        quote.setDescription(dto.getDescription());
        quote.setColored(dto.isColored());
        quote.setSize(dto.getSize());
        quote.setBodyPart(dto.getBodyPart());
        quote.setState(dto.getState());
        return quote;
    }

    public static void updateEntity(QuoteModel quote, CreateQuoteDto dto) {
        if (dto.getDescription() != null) quote.setDescription(dto.getDescription());
        if (dto.getSize() != null) quote.setSize(dto.getSize());
        if (dto.getBodyPart() != null) quote.setBodyPart(dto.getBodyPart());
        quote.setColored(dto.isColored());
        if (dto.getState() != null) quote.setState(dto.getState());
    }
}
