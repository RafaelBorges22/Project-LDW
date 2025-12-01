package ldw.squad.project.Dto;

import ldw.squad.project.Entities.Enums.BodyPart;
import ldw.squad.project.Entities.Enums.Size;
import ldw.squad.project.Entities.Enums.State;

import java.time.LocalDateTime;
import java.util.UUID;

public record UpdateQuoteDto(
        UUID clientId,
        Boolean colored,
        String description,
        Size size,
        BodyPart bodyPart,
        State state,
        LocalDateTime scheduleDate,
        Double additionalCost,
        Double finalValue) {
}
