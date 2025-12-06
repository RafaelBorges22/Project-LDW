package ldw.squad.project.Dto;
import ldw.squad.project.Entities.Enums.State;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateQuoteDto {
    private Double finalValue;
    private Double additionalCost;
    private State state;
}

