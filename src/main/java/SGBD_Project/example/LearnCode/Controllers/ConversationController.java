package SGBD_Project.example.LearnCode.Controllers;


import SGBD_Project.example.LearnCode.Dto.ConversationDto;
import SGBD_Project.example.LearnCode.Models.Conversation;
import SGBD_Project.example.LearnCode.Models.MessagePair;
import SGBD_Project.example.LearnCode.Security.JwtUtil;
import SGBD_Project.example.LearnCode.Services.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/v1/conversations/")
public class ConversationController {

    private final ConversationService conversationService;
    private final JwtUtil jwtUtil;


    @Autowired
    public ConversationController(ConversationService conversationService, JwtUtil jwtUtil) {
        this.conversationService = conversationService;
        this.jwtUtil = jwtUtil;
    }


    @PostMapping("getConversations")
    public ResponseEntity<?> getConversations(@RequestBody Map<String,Object> requestBody,@RequestHeader ("Authorization") String authorizationHeader ) {
        String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
        String email = jwtUtil.extractEmail(token);
        Map<String,Object> msg=new HashMap<>();
        if(email.isBlank()){
            msg.put("message","No email in the token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(msg);
        }
        int conversationsQt = (int) requestBody.get("conversationsQt");
        if(conversationsQt<=0){
            msg.put("message","Conversation Qt must be greater than 0");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }
        try{
            List<ConversationDto> conversationDtos=conversationService.getConversations(email,conversationsQt);
            msg.put("conversations",conversationDtos);
            msg.put("message","Successfully retrieved conversations");
            return ResponseEntity.status(HttpStatus.OK).body(msg);

        }
        catch(Exception e){
            msg.put("message",e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }

 }

    @GetMapping("getConversationById/{conversationId}")
    public ResponseEntity<?> getConversationById(@PathVariable Long conversationId, @RequestHeader ("Authorization") String authorizationHeader ) {
        String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
        String email = jwtUtil.extractEmail(token);
        Map<String,Object> msg=new HashMap<>();
        if(email.isBlank()){
            msg.put("message","No email in the token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(msg);
        }
        if(conversationId<=0){
            msg.put("message","ConversationId must be greater than 0");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }
        try{
            ConversationDto conversationDto=conversationService.getConversationById(email,conversationId);
            msg.put("conversation",conversationDto);
            msg.put("message","Successfully retrieved conversation");
            return ResponseEntity.status(HttpStatus.OK).body(msg);

        }
        catch(Exception e){
            msg.put("message",e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }
    }

    @PostMapping("createConversation")
    public ResponseEntity<?> createConversation(@RequestBody  MessagePair messagePair, @RequestHeader ("Authorization") String authorizationHeader ) {
        String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
        String email = jwtUtil.extractEmail(token);
        Map<String,Object> msg=new HashMap<>();
        if(email.isBlank()){
            msg.put("message","No email in the token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(msg);
        }
        if(messagePair==null){
            msg.put("message","No message pair");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }
        if( messagePair.getQuestion().isBlank()){
            msg.put("message","No question in the message pair");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }
        try{
            ConversationDto conversationDto=conversationService.createConversation(email,messagePair);
            msg.put("conversation",conversationDto);
            msg.put("message","Successfully created conversation");
            return ResponseEntity.status(HttpStatus.OK).body(msg);
        }
        catch(Exception e){
            msg.put("message",e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }
    }

    @PostMapping("addQuestion")
    public ResponseEntity<?> addQuestion(@RequestBody  Map<String,Object> requestBody, @RequestHeader ("Authorization") String authorizationHeader ) {
        String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
        String email = jwtUtil.extractEmail(token);
        System.out.println("userId is "+email);
        Map<String,Object> msg=new HashMap<>();
        if(email.isBlank()){
            msg.put("message","No email in the token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(msg);
        }
        Number id = (Number) requestBody.get("conversationId");
        Long conversationId = id.longValue();
        String question = requestBody.get("question").toString();
        if(conversationId<=0){
            msg.put("message","ConversationId must be greater than 0");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }
        if(question.isBlank()){
            msg.put("message","No question in the message pair");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }
        try{

            ConversationDto conversationDto=conversationService.addQuestion(email,question,conversationId);
            msg.put("message","Successfully added question to the conversation");
            msg.put("conversation",conversationDto);
            return ResponseEntity.status(HttpStatus.OK).body(msg);

        }catch (Exception e){
            msg.put("message",e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }

    }
}
