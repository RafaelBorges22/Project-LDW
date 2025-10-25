package ldw.squad.project.Entities;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name= "UUID", strategy="org.hibernate.id.UUIDGenerator")
    private UUID id;

    private String name;
    private String email;
    private String password;
    private String address;
    private String phone;
    private String role;

    //Esqueceu senha
    private String resetPasswordToken;
    private LocalDateTime resetPasswordTokenExpiry;
}
