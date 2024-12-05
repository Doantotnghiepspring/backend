package code.repository;

import code.model.more.Conversation;
import code.model.more.Message;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

  Page<Message> findALlByConversation(Pageable pageable,Conversation conversation);
  @Modifying
  @Transactional
  @Query("UPDATE Message m SET m.seen = true WHERE m.senderId = :senderId AND m.seen = false "
      + "AND m.senderRole = :senderRole")
  int seenMessageFromCustomer(long senderId, String senderRole);

  @Modifying
  @Transactional
  @Query("UPDATE Message m SET m.seen = true WHERE m.senderId = :senderId AND m.seen = false "
      + "AND m.senderRole = :senderRole "
      + "AND m.receiverId = :receiverId "
      + "AND m.senderRole = :receiverRole")
  int seenMessageFromAdmin(long senderId, String senderRole, long receiverId,String receiverRole);
}
