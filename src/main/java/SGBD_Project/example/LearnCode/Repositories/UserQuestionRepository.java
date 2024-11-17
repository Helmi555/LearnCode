package SGBD_Project.example.LearnCode.Repositories;

import SGBD_Project.example.LearnCode.Models.Question;
import SGBD_Project.example.LearnCode.Models.UserQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserQuestionRepository extends JpaRepository<UserQuestion, Long> {
    List<UserQuestion> findByQuestion_IdIn(List<String> questionIds);
    UserQuestion findByQuestion_IdAndUser_Id(String questionId, String userId);

    @Query(value = "SELECT * FROM user_question WHERE user_id = :userId AND question_id IN :questionIds", nativeQuery = true)
    List<UserQuestion> findByUserIdAndQuestionIdsNative(@Param("userId") String userId, @Param("questionIds") List<String> questionIds);

}
