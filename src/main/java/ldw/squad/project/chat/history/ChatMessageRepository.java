package ldw.squad.project.chat.history;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import org.springframework.data.mongodb.repository.Query;

public interface ChatMessageRepository extends MongoRepository<ChatMessageDocument, String> {

    @Query("{ $or: [ " +
           "{ $and: [ {'idUsuarioRemetente': ?0}, {'idUsuarioDestinatario': ?1} ] }, " +
           "{ $and: [ {'idUsuarioRemetente': ?1}, {'idUsuarioDestinatario': ?0} ] } " +
           "] }")
    List<ChatMessageDocument> findChatBetweenUsers(String user1, String user2);
}
