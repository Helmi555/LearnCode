package SGBD_Project.example.LearnCode.Services;

import SGBD_Project.example.LearnCode.Dto.QuestionnaireDto;

import java.util.List;
import java.util.Map;

public interface QuestionnaireService {
    QuestionnaireDto correctQuestionnaire(String email, Long questionnaireId, List<Map<String, Object>> questions);

    List<QuestionnaireDto> getAllQuestionnaires(String email);
}
