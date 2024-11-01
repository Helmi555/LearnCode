package SGBD_Project.example.LearnCode.Models;

import SGBD_Project.example.LearnCode.Utils.IdGenerator;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Question")
public class Question {
    @Id
    private String Id= IdGenerator.generateId();
    private String question;
    private int difficulty_level;
    private String hint;
    private String explanation;
    @CreationTimestamp
    @Column(updatable = false)  // Creation date should not be updated
    private LocalDateTime createdAt;  // First date: When the question is created

    @UpdateTimestamp
    private LocalDateTime updatedAt;  // Second date: When the question is last updated

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @ElementCollection
    private List<String> propositions=new ArrayList<>();
    @ElementCollection
    private List<String> answers=new ArrayList<>();
    @OneToMany(mappedBy = "question")
    private Set<UserQuestion> userQuestions;

    public boolean canAddProposition() {
        return propositions.size() < 4;
    }

}
