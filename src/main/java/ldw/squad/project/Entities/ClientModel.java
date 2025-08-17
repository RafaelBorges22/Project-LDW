package ldw.squad.project.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "clients")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class ClientModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private String name;
    private String email;
    private String password;
    private String adress;
}
