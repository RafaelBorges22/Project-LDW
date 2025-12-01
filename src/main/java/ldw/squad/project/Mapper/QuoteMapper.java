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
                quote.getClient().getEmail(),
                quote.getScheduleDate()
        );
    }

    public static QuoteModel toEntity(CreateQuoteDto dto) {
        QuoteModel quote = new QuoteModel();
        quote.setDescription(dto.description());
        quote.setColored(dto.colored());
        quote.setSize(dto.size());
        quote.setBodyPart(dto.bodyPart());
        quote.setState(dto.state());
        quote.setScheduleDate(dto.scheduleDate());
        return quote;
    }

    public static void updateEntity(QuoteModel quote, UpdateQuoteDto dto) {
        if (dto.description() != null) quote.setDescription(dto.description());
        if (dto.size() != null) quote.setSize(dto.size());
        if (dto.bodyPart() != null) quote.setBodyPart(dto.bodyPart());
        quote.setColored(dto.colored());
        if (dto.state() != null) quote.setState(dto.state());
        if (dto.scheduleDate() != null) quote.setScheduleDate(dto.scheduleDate());
        if(dto.finalValue()!=null) quote.setFinalValue((dto.finalValue()));
    }
}
