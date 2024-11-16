package SGBD_Project.example.LearnCode.Repositories;

import SGBD_Project.example.LearnCode.Models.PostUserAction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostUserRepository extends JpaRepository<PostUserAction,Long> {
    Optional<PostUserAction> findByUser_IdAndPost_Id(String userId, Long postId);
}
