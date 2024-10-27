package SGBD_Project.example.LearnCode.Repositories;

import SGBD_Project.example.LearnCode.Models.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface TopicRepository extends JpaRepository<Topic, Integer> {
    Topic findByName(String name); // Method to find a topic by name
    Set<Topic> findByIdIn(Set<Integer> ids);

}
