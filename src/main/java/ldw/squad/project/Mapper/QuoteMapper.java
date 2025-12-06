package ldw.squad.project.Mapper;

import ldw.squad.project.Dto.CreateQuoteDto;
import ldw.squad.project.Dto.QuoteDto;
import ldw.squad.project.Dto.UpdateQuoteDto;
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

    public static void updateEntity(QuoteModel entity, UpdateQuoteDto dto) {
        if (dto.getFinalValue() != null) entity.setFinalValue(dto.getFinalValue());
        if (dto.getAdditionalCost() != null) entity.setAdditionalCost(dto.getAdditionalCost());
        if (dto.getState() != null) entity.setState(dto.getState());
    }

}
