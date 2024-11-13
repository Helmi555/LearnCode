package SGBD_Project.example.LearnCode.Controllers;

import SGBD_Project.example.LearnCode.Dto.QuestionDto;
import SGBD_Project.example.LearnCode.Dto.TopicDto;
import SGBD_Project.example.LearnCode.Dto.UserEntityDto;
import SGBD_Project.example.LearnCode.Models.Question;
import SGBD_Project.example.LearnCode.Repositories.QuestionRepository;
import SGBD_Project.example.LearnCode.Repositories.UserRepository;
import SGBD_Project.example.LearnCode.Security.JwtUtil;
import SGBD_Project.example.LearnCode.Services.FlaskService;
import SGBD_Project.example.LearnCode.Services.QuestionService;
import SGBD_Project.example.LearnCode.Services.UserService;
import netscape.javascript.JSObject;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/api/v1/users/")
public class UserController {

    private final FlaskService flaskService;
    private final UserService userService;
    private final JwtUtil  jwtUtil;
    private final QuestionService questionService;

    @Autowired
    public UserController(FlaskService flaskService, UserService userService, JwtUtil jwtUtil,QuestionService questionService) {
        this.flaskService = flaskService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.questionService = questionService;
    }


    //////// Pour corriges les questions
    /*
    {
        question:{
        questionId:"1dz8zPm6",
        answersId:["2"]
        },
        .
        .
        .
    } * */


    @PostMapping("saveSelectedTopics")
    public ResponseEntity<?> saveSelectedTopics(@RequestBody UserEntityDto userEntityDto, @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        String email = jwtUtil.extractEmail(token);
        Map<String,Object> msg = new HashMap<>();
        if(email == null || email.isEmpty()) {
            msg.put("message", "Invalid email");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }
        Set<Integer> topicsId = userEntityDto.getTopicsId();
        if(topicsId==null || topicsId.size()<1) {
            msg.put("message", "Please enter at least 1 topic");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }
        try{
            Set<TopicDto> topicDtos= userService.saveSelectedTopics(email,topicsId);
            msg.put("message", "Successfully added topics");
            msg.put("topics",topicDtos);
            return ResponseEntity.status(HttpStatus.OK).body(msg);
        }
        catch(Exception e){
            msg.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }
    }

    @PostMapping("updateUserRanks")
    public ResponseEntity<?> updateUserRanks(@RequestBody List<Map<String,Object>> topics, @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        String email = jwtUtil.extractEmail(token); // Get username from token
        Map<String,Object> msg = new HashMap<>();
        if(email == null || email.isEmpty()) {
            msg.put("message", "Invalid email");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }
        if(topics == null || topics.size()<3) {
            msg.put("message", "Please enter at least 3 topics");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }

        try{
            userService.updateUserRanks(email,topics);
            msg.put("message", "Successfully updated user ranks");
            return ResponseEntity.status(HttpStatus.OK).body(msg);

        }catch (Exception e){
            msg.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }
    }
}





