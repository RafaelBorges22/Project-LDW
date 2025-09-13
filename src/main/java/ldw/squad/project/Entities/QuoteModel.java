package ldw.squad.project.Entities;

import jakarta.persistence.*;
import ldw.squad.project.Entities.Enums.BodyPart;
import ldw.squad.project.Entities.Enums.Size;
import ldw.squad.project.Entities.Enums.State;
import lombok.*;

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
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private Size size;

    @Enumerated(EnumType.STRING)
    private BodyPart bodyPart;

    @Enumerated(EnumType.STRING)
    private State state;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private ClientModel client;
}
