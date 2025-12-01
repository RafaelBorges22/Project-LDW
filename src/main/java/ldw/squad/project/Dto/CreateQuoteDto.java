package ldw.squad.project.Dto;

import java.time.LocalDateTime;
import java.util.UUID;

import ldw.squad.project.Entities.Enums.BodyPart;
import ldw.squad.project.Entities.Enums.Size;
import ldw.squad.project.Entities.Enums.State;

public record CreateQuoteDto(
        UUID clientId,
        boolean colored,
        String description,
        Size size,
        BodyPart bodyPart,
        State state,
        LocalDateTime scheduleDate
) {}

