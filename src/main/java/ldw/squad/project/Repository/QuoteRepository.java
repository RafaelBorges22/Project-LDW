package ldw.squad.project.Repository;

import ldw.squad.project.Entities.ClientModel;
import org.springframework.data.jpa.repository.JpaRepository;

import ldw.squad.project.Entities.QuoteModel;

import java.util.List;

public interface QuoteRepository extends JpaRepository<QuoteModel, Long> {
    List<QuoteModel> findByClient(ClientModel client);
}
