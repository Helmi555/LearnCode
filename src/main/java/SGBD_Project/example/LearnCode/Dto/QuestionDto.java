package SGBD_Project.example.LearnCode.Dto;


import SGBD_Project.example.LearnCode.Models.Question;
import SGBD_Project.example.LearnCode.Utils.IdGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class QuestionDto {

    private String Id;
    private String question;
    private int difficulty_level;
    private String hint;
    private String explanation;
    private String topicName;
    private List<String> propositions=new ArrayList<>();
    private List<String> answers=new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public boolean isValidPropositions(){
            if(propositions.size()!=4){
                return false;
            }
            for(String proposition:propositions){
                if(proposition.isBlank()){
                    return false;
                }
            }
            return true;
    }

    public boolean isValidAnswers(){
        if(answers.isEmpty()){
            return false;
        }
        for(String answer:answers){
            if(answer.isBlank()){
                return false;
            }
            if(!propositions.contains(answer)){
                return false;
            }
        }
        return true;
    }


    public static QuestionDto toDto(Question question) {
        return QuestionDto.builder()
                .Id(question.getId().toString())
                .question(question.getQuestion())
                .difficulty_level(question.getDifficulty_level())
                .hint(question.getHint())
                .explanation(question.getExplanation())
                .topicName(question.getTopic() != null ? question.getTopic().getName() : null)
                .propositions(new ArrayList<>(question.getPropositions()))
                .answers(new ArrayList<>(question.getAnswers()))
                .createdAt(question.getCreatedAt())
                .updatedAt(question.getUpdatedAt())
                .build();
    }

    public static Question toEntity(QuestionDto questionDto) {
        return Question.builder()
                .Id(questionDto.getId() != null ? questionDto.getId() : null)
                .question(questionDto.getQuestion())
                .difficulty_level(questionDto.getDifficulty_level())
                .hint(questionDto.getHint())
                .explanation(questionDto.getExplanation())
                // Assuming you have a service or method to fetch Topic by name
                // .topic(topicService.findByName(questionDto.getTopicName()))
                .propositions(new ArrayList<>(questionDto.getPropositions()))
                .answers(new ArrayList<>(questionDto.getAnswers()))
                .createdAt(questionDto.getCreatedAt())
                .updatedAt(questionDto.getUpdatedAt())
                .build();
    }

    public static Map<String, Object> toMapDto(Question question,Map<Integer,String> propositions) {
        Map<String, Object> questionDtoMap = new HashMap<>();

        questionDtoMap.put("id", question.getId() != null ? question.getId().toString() : null);
        questionDtoMap.put("question", question.getQuestion());
        questionDtoMap.put("difficulty_level", question.getDifficulty_level());
        questionDtoMap.put("hint", question.getHint());
        questionDtoMap.put("explanation", question.getExplanation());
        questionDtoMap.put("topicName", question.getTopic() != null ? question.getTopic().getName() : null);
        questionDtoMap.put("propositions", propositions);
        //questionDtoMap.put("answers", new ArrayList<>(question.getAnswers()));
        questionDtoMap.put("createdAt", question.getCreatedAt());
        questionDtoMap.put("updatedAt", question.getUpdatedAt());

        return questionDtoMap;
    }

}


