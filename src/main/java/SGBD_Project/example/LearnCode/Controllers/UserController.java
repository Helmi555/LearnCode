package SGBD_Project.example.LearnCode.Controllers;

import SGBD_Project.example.LearnCode.Dto.QuestionDto;
import SGBD_Project.example.LearnCode.Dto.UserEntityDto;
import SGBD_Project.example.LearnCode.Models.Question;
import SGBD_Project.example.LearnCode.Security.JwtUtil;
import SGBD_Project.example.LearnCode.Services.FlaskService;
import SGBD_Project.example.LearnCode.Services.UserService;
import netscape.javascript.JSObject;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.HTML;
import java.util.*;

@Controller
@RequestMapping("/api/v1/users/")
public class UserController {

    private final FlaskService flaskService;
    private final UserService userService;
    private final JwtUtil  jwtUtil;
    @Autowired
    public UserController(FlaskService flaskService, UserService userService, JwtUtil jwtUtil) {
        this.flaskService = flaskService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }


    @GetMapping("getQuestionnaire")
    public ResponseEntity<?> getQuestionnaire(@RequestBody Map<String,Object> requestBody, @RequestHeader("Authorization") String authorizationHeader) {
        // Get user details, questions, and topics from the request
        String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
        String email = jwtUtil.extractEmail(token);
        List<Integer> topicsList = (List<Integer>) requestBody.get("topicsId");
        Map<String,Object> msg=new HashMap<>();
        if(topicsList == null || topicsList.isEmpty()){
            msg.put("message","Please enter some topics");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(msg);
        }

        Set<Integer> topics = new HashSet<>(topicsList);
        int questionQuantity=(Integer) requestBody.get("questionQuantity");

        try {
            List<Map<String,Object>> response = flaskService.sendRequestToFlask(email, topics,questionQuantity);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // Return an error response with a 400 status code
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    //////// aaa faireeeeeeeee
    @PostMapping("correctQuestionnaire")
    public ResponseEntity<?> correctQuestionnaire(@RequestBody List<Map<String,Object>> userAnswers, @RequestHeader("Authorization") String authorization) {
        Map<String,Object> msg=new HashMap<>();
        String token = authorization.substring(7);
        String email = jwtUtil.extractEmail(token);
        if(userAnswers.isEmpty()){
            msg.put("message","User answers are empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }
        return null;
    }

    @PostMapping("saveSelectedTopics")
    public ResponseEntity<?> saveSelectedTopics(@RequestBody UserEntityDto userEntityDto, @RequestHeader("Authorization") String authorizationHeader) {
        System.out.println("1111111111");
        String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
        String email = jwtUtil.extractEmail(token); // Get username from token
        System.out.println("222222");
        Map<String,Object> msg = new HashMap<>();
        if(email == null || email.isEmpty()) {
            msg.put("message", "Invalid email");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }
        Set<Integer> topicsId = userEntityDto.getTopicsId();
        if(topicsId==null || topicsId.size()<1) {
            msg.put("message", "Please enter at least 3 topics");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }
        try{
            System.out.println("333333333333");

            userService.saveSelectedTopics(email,topicsId);
            System.out.println("444444444444");

            msg.put("message", "Successfully added topics");
            return ResponseEntity.status(HttpStatus.OK).body(msg);
        }
        catch(Exception e){
            System.out.println("5555555555555");
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





