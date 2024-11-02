package SGBD_Project.example.LearnCode.Repositories;

import SGBD_Project.example.LearnCode.Models.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, String> {

    Optional<Question> findById(String id);
    List<Question> findByTopic_Id(int topic_Id);
    List<Question> findByTopic_IdIn(List<Integer> topic_Ids);
}
