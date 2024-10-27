package SGBD_Project.example.LearnCode.Repositories;

import SGBD_Project.example.LearnCode.Models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, String> {

    Optional<UserEntity> findByEmail(String email);
}
