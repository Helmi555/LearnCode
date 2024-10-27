package SGBD_Project.example.LearnCode.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "Topic")
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;

    @OneToMany(mappedBy = "topic",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    List<Question> questions=new ArrayList<>();

    @OneToMany(mappedBy = "topic",cascade = CascadeType.ALL)
    private Set<UserTopic> userTopics;
}
