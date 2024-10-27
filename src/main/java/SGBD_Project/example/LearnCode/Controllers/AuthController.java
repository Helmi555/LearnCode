package SGBD_Project.example.LearnCode.Controllers;

import SGBD_Project.example.LearnCode.Dto.UserEntityDto;
import SGBD_Project.example.LearnCode.Models.Topic;
import SGBD_Project.example.LearnCode.Repositories.UserRepository;
import SGBD_Project.example.LearnCode.Services.TopicService;
import SGBD_Project.example.LearnCode.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.token.TokenService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/v1/users/")
public class AuthController {

    UserService userService;
    TopicService topicService;


        @Autowired
        public AuthController(UserService userService, TopicService topicService) {
            this.userService = userService;
            this.topicService=topicService;
        }

        @PostMapping("signUp")
        public ResponseEntity<?> signUp(@RequestBody UserEntityDto userEntityDto) {
            Map<String,Object> msg = new HashMap<>();
            if(!userEntityDto.checkError()) {
                msg.put("message", "Please check all the fields");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
            }
            if(!UserEntityDto.isValidAddress(userEntityDto.getAddress())){
                msg.put("message", "Please enter a valid address");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
            }
            if(!UserEntityDto.isValidEmail(userEntityDto.getEmail())){
                msg.put("message", "Please enter a valid email");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
            }
            if (!UserEntityDto.isValidAge(userEntityDto.getAge())){
                msg.put("message", "Please enter a valid age");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
            }
            if (!UserEntityDto.isValidName(userEntityDto.getName()) || !UserEntityDto.isValidName(userEntityDto.getLastName())){
                msg.put("message", "Please enter a valid name/lastName");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
            }
            if(!UserEntityDto.isValidBirthDay(userEntityDto.getBirthDay())){
                msg.put("message", "Please enter a valid birthday");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
            }
            if(userEntityDto.getPassword().length()<4){
                msg.put("message", "Please enter a valid password");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
            }
            if(!UserEntityDto.isValidTopics(userEntityDto.getTopicsId())){
                msg.put("message", "Please enter at least 3 topics");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
            }
            if(!topicService.checkTopicsExistence(userEntityDto.getTopicsId())){
                msg.put("message", "Please enter valid topics ids");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
            }
            try {
                UserEntityDto savedUserDto = userService.createUser(userEntityDto);
                msg.put("message", "User created successfully");
                Map<String,Object>userMap = savedUserDto.userMap();
                userMap.put("topicsId",userEntityDto.getTopicsId());
                msg.put("User", userMap);
                return ResponseEntity.status(HttpStatus.CREATED).body(msg);
            }catch(Exception e){
                msg.put("message", "Failed to create user. "+e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(msg);
            }
        }

        @GetMapping("getUserById/{userId}")
        public ResponseEntity<?> getUserById(@PathVariable("userId") String userId){
            Map<String,Object> msg = new HashMap<>();
            if( userId==null|| userId.isBlank() ){
                msg.put("message", "Please enter a valid id");
            }
            try {
                UserEntityDto user=userService.getUserById(userId);
                msg.put("user", user);
                return ResponseEntity.status(HttpStatus.OK).body(msg);

            }catch (Exception e){
                msg.put("message",e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(msg);
            }
        }

        @PostMapping("addQuestionToUser/{userId}")
        public ResponseEntity<?> addQuestionToUser(@PathVariable("userId") String userId){
            Map<String,Object> msg = new HashMap<>();
            if( userId==null|| userId.isBlank() ){
                msg.put("message", "Please enter a valid id");
            }
            try {
                userService.addQuesToUser(userId);
                msg.put("message", "Question added to user successfully");
                return ResponseEntity.status(HttpStatus.CREATED).body(msg);

            }
            catch (Exception e){
                msg.put("message",e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(msg);
            }
        }


        @PostMapping("addListOfUsers")
    public ResponseEntity<?> addListOfUsers(@RequestBody List<UserEntityDto> list) {
            List<String> responseList = new ArrayList<>();
            try {
                for (UserEntityDto userDto : list) {
                    String res=addUsers(userDto);
                    responseList.add(res);
                }
                return ResponseEntity.status(HttpStatus.OK).body(responseList);
            }
            catch (Exception e){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }

        }
    public String addUsers(UserEntityDto userEntityDto) {
        if (!userEntityDto.checkError()) {
            return "Please check all the fields";
        }

        if (!UserEntityDto.isValidAddress(userEntityDto.getAddress())) {
            return "Please enter a valid address";
        }

        if (!UserEntityDto.isValidEmail(userEntityDto.getEmail())) {
            return "Please enter a valid email";
        }

        if (!UserEntityDto.isValidAge(userEntityDto.getAge())) {
            return "Please enter a valid age";
        }

        if (!UserEntityDto.isValidName(userEntityDto.getName()) || !UserEntityDto.isValidName(userEntityDto.getLastName())) {
            return "Please enter a valid name/lastName";
        }

        if (!UserEntityDto.isValidBirthDay(userEntityDto.getBirthDay())) {
            return "Please enter a valid birthday";
        }

        if (userEntityDto.getPassword().length() < 4) {
            return "Please enter a valid password";
        }

        if (!UserEntityDto.isValidTopics(userEntityDto.getTopicsId())) {
            return "Please enter at least 3 topics";
        }

        if (!topicService.checkTopicsExistence(userEntityDto.getTopicsId())) {
            return "Please enter valid topics ids";
        }

        try {
            System.out.println("hiiiiiiiiiiiiiiiiiiiiiiiiiii");
            UserEntityDto savedUserDto = userService.createUser(userEntityDto);
            System.out.println("5555555555555555555555555555555555555555555555555555");
            return "User created successfully"+savedUserDto.getId()+savedUserDto.getEmail();
        } catch (Exception e) {
            return "Failed to create user in addusers. " + e.getMessage();
        }
    }


}
