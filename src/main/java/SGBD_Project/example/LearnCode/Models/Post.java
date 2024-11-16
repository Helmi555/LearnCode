package SGBD_Project.example.LearnCode.Models;


import SGBD_Project.example.LearnCode.Models.Enums.Type;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jdk.jfr.Category;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "post")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String author;

    private LocalDateTime date;

    @ElementCollection
    private List<String> tags;

    @ElementCollection
    private List<String> links;

    @Enumerated(EnumType.STRING)
    private Type type;

    @UpdateTimestamp
    private Timestamp updatedAt;
    @CreationTimestamp
    private Timestamp createdAt;

    @ElementCollection
    private List<String> mediaUrls;

    private Integer upVoteNumber = 0;

    private Integer downVoteNumber = 0;

    private Integer seenNumber = 0;

    private Boolean isActive = true;

    @OneToMany(mappedBy = "post", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    @JsonIgnoreProperties("post") // Add this annotation
    private Set<PostUserAction> actions = new HashSet<>();



}
