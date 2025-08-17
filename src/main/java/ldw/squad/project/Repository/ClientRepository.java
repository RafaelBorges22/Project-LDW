package ldw.squad.project.Repository;

import ldw.squad.project.Entities.ClientModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<ClientModel, Long> {
}
