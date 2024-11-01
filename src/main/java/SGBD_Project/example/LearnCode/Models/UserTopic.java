package SGBD_Project.example.LearnCode.Models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"user"}) // Simplified annotation
public class UserTopic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // Added LAZY loading
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore // Changed to @JsonIgnore
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY) // Added LAZY loading
    @JoinColumn(name = "topic_id")
    private Topic topic;

    private double rank;

    @Override
    public String toString() {
        return "UserTopic{" +
                "id=" + id +
                ", topic=" + (topic != null ? topic.getId() : null) +
                ", rank=" + rank +
                '}';
    }

    public static double getRankFromLevel(int level) {
            if(level == 1) return 0;
            if(level == 2) return 0.2;
            if(level == 3) return 0.4;
            else return 0.6;
    }
}