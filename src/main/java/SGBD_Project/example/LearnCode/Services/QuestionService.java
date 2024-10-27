package SGBD_Project.example.LearnCode.Services;

import SGBD_Project.example.LearnCode.Dto.QuestionDto;

import java.util.List;

public interface QuestionService {

    QuestionDto createQuestion(QuestionDto questionDto);
    List<QuestionDto> getAllQuestions();
    QuestionDto getQuestionById(String id);
}
