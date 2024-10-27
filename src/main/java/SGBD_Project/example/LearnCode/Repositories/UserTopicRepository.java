package SGBD_Project.example.LearnCode.Repositories;

import SGBD_Project.example.LearnCode.Models.UserTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface UserTopicRepository extends JpaRepository<UserTopic, Integer> {

    Set<UserTopic> findByUser_Id(String userId);


    @Query(value = "SELECT topic_id FROM user_topic WHERE user_id = :userId", nativeQuery = true)
    Set<Integer> findUserTopicsByUserId(@Param("userId") String userId);
}
