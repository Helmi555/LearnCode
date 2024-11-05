package SGBD_Project.example.LearnCode.Repositories;


import SGBD_Project.example.LearnCode.Models.Questionnaire;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionnaireRepository  extends JpaRepository<Questionnaire, Long> {


}
