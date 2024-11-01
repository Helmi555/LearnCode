package SGBD_Project.example.LearnCode.Models;


import SGBD_Project.example.LearnCode.Utils.IdGenerator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@Table(name="users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {
    @Id
    private String id = IdGenerator.generateId();
    private String name;
    private String lastName;
    private String email;
    private String password;
    private LocalDate birthDay;
    private int age;
    private String address;
    private Boolean isBanned;
    private LocalDateTime lastLoginDate;

    @CreationTimestamp
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime updatedDate;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY) // Changed to LAZY
    private List<UserQuestion> userQuestions;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY) // Changed to LAZY
    private Set<UserTopic> userTopics = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "user_token", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "token")
    private List<String> tokens = new ArrayList<>();

    public void addToken(String token) {
        tokens.add(token);
    }

    public void removeToken(String token) {
        tokens.remove(token);
    }

    // Add helper method for managing UserTopics
    public void addUserTopic(UserTopic userTopic) {
        userTopics.add(userTopic);
        userTopic.setUser(this);
    }
    // Override toString to prevent circular reference
    @Override
    public String toString() {
        return "UserEntity{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
