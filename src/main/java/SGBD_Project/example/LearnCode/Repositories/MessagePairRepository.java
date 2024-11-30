package SGBD_Project.example.LearnCode.Repositories;


import SGBD_Project.example.LearnCode.Models.MessagePair;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessagePairRepository extends JpaRepository<MessagePair, Long> {

    @Query(value = "SELECT * FROM message_pair WHERE conversation_id = :conversationId ORDER BY created_at DESC LIMIT :limit", nativeQuery = true)
    List<MessagePair> findTopMessagesByConversationId(@Param("conversationId") Long conversationId, @Param("limit") int limit);

    @Query(value = "SELECT m FROM MessagePair m WHERE m.conversation.id = :conversationId AND (:excludedIds IS NULL OR m.id NOT IN :excludedIds) ORDER BY m.createdAt DESC")
    List<MessagePair> findTopMessagesByConversationIdExcludingIds(
            @Param("conversationId") Long conversationId,
            @Param("excludedIds") List<Long> excludedIds,
            Pageable pageable
    );

    MessagePair findByIdAndConversation_Id(Long messageId, Long conversationId);

}
