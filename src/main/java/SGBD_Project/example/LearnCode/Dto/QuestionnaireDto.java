package SGBD_Project.example.LearnCode.Dto;

import SGBD_Project.example.LearnCode.Models.Question;
import SGBD_Project.example.LearnCode.Models.Questionnaire;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionnaireDto {
    private Long id;

    private LocalDateTime creationTime;
    private boolean completed ;
    private int numberOfQuestions ;
    private int numberCorrectedAnswers ;
    private String description;
    private Set<Question> questions ;
    private LocalDateTime answeredAt ;


    public static QuestionnaireDto toDto(Questionnaire questionnaire) {
        return QuestionnaireDto.builder()
                .id(questionnaire.getId())
                .creationTime(questionnaire.getCreationTime())
                .completed(questionnaire.isCompleted())
                .numberOfQuestions(questionnaire.getNumberOfQuestions())
                .numberCorrectedAnswers(questionnaire.getNumberCorrectedAnswers())
                .description(questionnaire.getDescription())
                .questions(questionnaire.getQuestions())
                .answeredAt(questionnaire.getAnsweredAt())
                .build();
    }

    public static Questionnaire toMap(QuestionnaireDto dto) {
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setId(dto.getId());
        questionnaire.setCreationTime(dto.getCreationTime());
        questionnaire.setCompleted(dto.isCompleted());
        questionnaire.setNumberOfQuestions(dto.getNumberOfQuestions());
        questionnaire.setNumberCorrectedAnswers(dto.getNumberCorrectedAnswers());
        questionnaire.setDescription(dto.getDescription());
        questionnaire.setQuestions(dto.getQuestions());
        questionnaire.setAnsweredAt(dto.getAnsweredAt());
        return questionnaire;
    }


}
