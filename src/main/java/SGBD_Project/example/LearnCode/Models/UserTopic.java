package SGBD_Project.example.LearnCode.Models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"user","topic"}) // Ignore user field during serialization

public class UserTopic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonBackReference // This prevents recursion when serializing
    @JoinColumn(name = "user_id")
    @JsonIgnore // Prevent serialization of UserEntity in UserTopic
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "topic_id")
    private Topic topic;

    private double rank;
}
