package SGBD_Project.example.LearnCode.Repositories;

import SGBD_Project.example.LearnCode.Models.Conversation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation,Long> {
    @Query("SELECT c FROM Conversation c WHERE c.user.id = :userId ORDER BY c.createdAt DESC")
    List<Conversation> findTopConversationsByUserId(@Param("userId") String userId, Pageable pageable);

    @Query(value = "SELECT * FROM conversation WHERE user_id = :userId ORDER BY created_at DESC LIMIT :limit", nativeQuery = true)
    List<Conversation> findRecentConversationsByUserId(@Param("userId") String userId, @Param("limit") int limit);


    Optional<Conversation> findByIdAndUserId( Long conversationId,String userId);
}
