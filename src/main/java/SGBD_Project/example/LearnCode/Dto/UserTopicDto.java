package SGBD_Project.example.LearnCode.Dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserTopicDto {
    private int id;
    private String userId;
    private int topicId;
    private Double rank;
    private boolean activated;
}
