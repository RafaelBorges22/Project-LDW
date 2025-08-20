package ldw.squad.project.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "quotes")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class QuoteModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private boolean isColored;
    private String description;
    private Double additionalCost;
    private Double finalValue;

    @Enumerated(EnumType.STRING)
    private Size size;

    @Enumerated(EnumType.STRING)
    private BodyPart bodyPart;

    public enum Size {
        SMALL,
        MEDIUM,
        LARGE
    }

    public enum BodyPart {
        ARM,
        BACK,
        LEG,
        CHEST,
        RIB,
        NECK,
        HAND,
        HEAD,
        FOOT,
        OTHER
    }
    
    public enum State {
        WAITING,
        AWNSERED,
        PAID
    }
}
