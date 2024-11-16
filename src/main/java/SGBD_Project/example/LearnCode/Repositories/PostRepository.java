package SGBD_Project.example.LearnCode.Repositories;

import SGBD_Project.example.LearnCode.Models.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // Find posts that have a specific tag
    @Query("SELECT p FROM Post p JOIN p.tags t WHERE t = :tag")
    List<Post> findByTag(@Param("tag") String tag);

    // Optional: Find IDs of posts that have a specific tag
    @Query("SELECT p.id FROM Post p JOIN p.tags t WHERE t = :tag AND p.isActive=true")
    List<Long> findPostIdsByTag(@Param("tag") String tag);

}
