package SGBD_Project.example.LearnCode.Dto;

import SGBD_Project.example.LearnCode.Models.Question;
import SGBD_Project.example.LearnCode.Models.UserEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserQuestionDto {

    private Long id;
    private String userId;
    private String questionId;
    private int topicId;
    private boolean given;
    private double correctness;
    private int respondingTime;
    private double levelQuestion;


}
