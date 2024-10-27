package SGBD_Project.example.LearnCode.Dto;


import SGBD_Project.example.LearnCode.Utils.IdGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


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
}
