package SGBD_Project.example.LearnCode.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table( name = "post_user_actions",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"post_id", "user_id"})
        })

public class PostUserAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    private Boolean upvoted = false;

    private Boolean downvoted = false;

    private Boolean seen = false;

    private Timestamp seenAt;

    @CreationTimestamp
    private Timestamp createdAt;
    @UpdateTimestamp
    private Timestamp updatedAt;

    // Constructors, getters, setters
}
