package SGBD_Project.example.LearnCode.Models;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MessagePair {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String question;
    private String answer;

    private boolean isValid = true;

    @CreationTimestamp
    private Timestamp createdAt;
    @UpdateTimestamp
    private Timestamp updatedAt;

    private int isLiked=0;

    @ManyToOne
    @JoinColumn(name = "conversation_id")
    @JsonBackReference
    private Conversation conversation;

    public void setIsLiked(int isLiked) {
        if (isLiked < -1 || isLiked > 1) {
            throw new IllegalArgumentException("Invalid value for isLiked. Allowed: -1, 0, 1.");
        }
        this.isLiked = isLiked;
    }

}
