package SGBD_Project.example.LearnCode.Controllers;


import SGBD_Project.example.LearnCode.Dto.QuestionnaireDto;
import SGBD_Project.example.LearnCode.Security.JwtUtil;
import SGBD_Project.example.LearnCode.Services.FlaskService;
import SGBD_Project.example.LearnCode.Services.QuestionnaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/api/v1/questionnaire")
public class QuestionnaireController {

    private final QuestionnaireService questionnaireService;
    private final JwtUtil jwtUtil;
    private final FlaskService flaskService;


    @Autowired
    public QuestionnaireController(QuestionnaireService questionnaireService, JwtUtil jwtUtil, FlaskService flaskService) {
        this.questionnaireService = questionnaireService;
        this.jwtUtil = jwtUtil;
        this.flaskService = flaskService;
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
            Map<String,Object> response = flaskService.sendRequestToFlask(email, topics,questionQuantity);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // Return an error response with a 400 status code
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }
    @PostMapping("correctQuestionnaire")
    public ResponseEntity<?> correctQuestionnaire(@RequestBody Map<String,Object> userAnswers, @RequestHeader("Authorization") String authorization) {
        Map<String,Object> msg=new HashMap<>();
        String token = authorization.substring(7);
        String email = jwtUtil.extractEmail(token);
        if(userAnswers.isEmpty()){
            msg.put("message","User answers are empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }
        System.out.println("correctQuestionnaire, User email: "+email);
        try{
            Long questionnaireId = ((Number) userAnswers.get("questionnaireId")).longValue();
            List<Map<String,Object>> questions=(List<Map<String,Object>>) userAnswers.get("questions");

            QuestionnaireDto questionDto =questionnaireService.correctQuestionnaire(email,questionnaireId,questions);
            msg.put("message","Note calculated successfully");
            msg.put("Questionnaire",questionDto);
            return ResponseEntity.ok(msg);

        }catch (Exception e){
            msg.put("message",e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }
    }


}
