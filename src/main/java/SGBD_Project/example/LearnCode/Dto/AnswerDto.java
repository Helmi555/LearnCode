package SGBD_Project.example.LearnCode.Dto;

import lombok.Data;

@Data
public class AnswerDto {
    private boolean given;
    private double correctness;
    private int respondingTime;
}
