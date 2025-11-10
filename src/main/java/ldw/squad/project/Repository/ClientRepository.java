package ldw.squad.project.Repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import ldw.squad.project.Entities.ClientModel;

public interface ClientRepository extends JpaRepository<ClientModel, UUID> {
    Optional<ClientModel> findByName(String name);
    Optional<ClientModel> findByEmail(String email);
    Optional<ClientModel> findByResetPasswordToken(String token);

    boolean existsByEmail(String email);
}
