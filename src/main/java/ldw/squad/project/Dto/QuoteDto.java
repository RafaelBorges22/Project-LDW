package ldw.squad.project.Dto;

import java.util.UUID;

import ldw.squad.project.Entities.Enums.BodyPart;
import ldw.squad.project.Entities.Enums.Size;
import ldw.squad.project.Entities.Enums.State;

public record QuoteDto(
        Long id,
        String description,
        String imageUrl,
        Size size,
        BodyPart bodyPart,
        State state,
        Double finalValue,
        boolean colored,
        Double additionalCost,
        UUID clientId,
        String clientName,
        String clientEmail
) {}
