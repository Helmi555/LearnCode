package SGBD_Project.example.LearnCode.Services;

import SGBD_Project.example.LearnCode.Dto.TopicDto;
import SGBD_Project.example.LearnCode.Dto.UserDto;
import SGBD_Project.example.LearnCode.Dto.UserEntityDto;

import java.util.List;
import java.util.Map;
import java.util.Set;


public interface UserService {
    UserEntityDto createUser(UserEntityDto userDto);

    UserEntityDto getUserById(String userId);

    void addQuesToUser(String userId);

    UserDto signIn(String email, String password);

    Boolean signOut(String token);

    Set<TopicDto> saveSelectedTopics(String email, Set<Integer> topicsId);

    void updateUserRanks(String email, List<Map<String, Object>> topics);

    UserDto getUserDetails(String email);
}
