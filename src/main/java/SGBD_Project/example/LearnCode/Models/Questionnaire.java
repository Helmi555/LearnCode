package SGBD_Project.example.LearnCode.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Questionnaire")
public class Questionnaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    private LocalDateTime creationTime;
    private boolean completed ;
    private int numberOfQuestions ;
    private int numberCorrectedAnswers ;
    private String description;
    private LocalDateTime answeredAt ;

    @ManyToMany
    @JoinTable(
            name = "Questionnaire_Question",
            joinColumns = @JoinColumn(name = "Questionnaire_id"),
            inverseJoinColumns = @JoinColumn(name = "Question_id")
    )
    private Set<Question> questions =new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

}
