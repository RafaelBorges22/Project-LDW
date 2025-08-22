package ldw.squad.project.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ldw.squad.project.Entities.ClientModel;
import ldw.squad.project.Entities.QuoteModel;

public interface QuoteRepository extends JpaRepository<QuoteModel, Long> {
}
