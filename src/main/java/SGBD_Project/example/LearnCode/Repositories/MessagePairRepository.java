package SGBD_Project.example.LearnCode.Repositories;


import SGBD_Project.example.LearnCode.Models.MessagePair;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessagePairRepository extends JpaRepository<MessagePair, Long> {

    @Query(value = "SELECT m FROM MessagePair m WHERE m.conversation.id = :conversationId ORDER BY m.createdAt DESC")
    List<MessagePair> findTopMessagesByConversationId(@Param("conversationId") Long conversationId, Pageable pageable);
}
