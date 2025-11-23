package ldw.squad.project.Dto;

import java.util.UUID;

import ldw.squad.project.Entities.Enums.BodyPart;
import ldw.squad.project.Entities.Enums.Size;
import ldw.squad.project.Entities.Enums.State;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateQuoteDto {
    private UUID clientId;
    private boolean colored;
    private String description;
    private Size size;
    private BodyPart bodyPart;
    private State state;
}
