package SGBD_Project.example.LearnCode.Services;

import SGBD_Project.example.LearnCode.Dto.UserEntityDto;


public interface UserService {
    UserEntityDto createUser(UserEntityDto userDto);

    UserEntityDto getUserById(String userId);

    void addQuesToUser(String userId);
}
