package SGBD_Project.example.LearnCode.Models;


import SGBD_Project.example.LearnCode.Utils.IdGenerator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name="users")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

    @Id
    private String id= IdGenerator.generateId();
    private String name;
    private String lastName;
    private String email;
    private String password;
    private LocalDate birthDay;
    private int age;
    private String address;
    private Boolean isBanned;

    @CreationTimestamp
    private LocalDateTime createdDate;
    @UpdateTimestamp
    private LocalDateTime  updatedDate;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JsonIgnoreProperties("user") // Prevent serialization of the user in UserTopic
    private List<UserQuestion> userQuestions;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference // This prevents recursion when serializing
    private Set<UserTopic> userTopics;



}
