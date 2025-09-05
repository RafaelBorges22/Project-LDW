package ldw.squad.project.Repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import ldw.squad.project.Entities.ClientModel;

public interface ClientRepository extends JpaRepository<ClientModel, Long> {
    Optional<ClientModel> findByName(String name);
    Optional<ClientModel> findByEmail(String email);
}