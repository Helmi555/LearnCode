package SGBD_Project.example.LearnCode.Repositories;

import SGBD_Project.example.LearnCode.Models.Question;
import SGBD_Project.example.LearnCode.Models.UserQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserQuestionRepository extends JpaRepository<UserQuestion, Long> {
    List<UserQuestion> findByQuestion_IdIn(List<String> questionIds);
}
