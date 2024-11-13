package SGBD_Project.example.LearnCode.Controllers;

import SGBD_Project.example.LearnCode.Dto.TopicDto;
import SGBD_Project.example.LearnCode.Models.Topic;
import SGBD_Project.example.LearnCode.Security.JwtUtil;
import SGBD_Project.example.LearnCode.Services.Impl.CloudinaryService;
import SGBD_Project.example.LearnCode.Services.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/api/v1/topics/")
public class TopicController {

    private final TopicService topicService;
    private  final CloudinaryService cloudinaryService;
    private final JwtUtil jwtUtil;

    @Autowired
    public TopicController(TopicService topicService, CloudinaryService cloudinaryService, JwtUtil jwtUtil) {
        this.topicService = topicService;
        this.cloudinaryService = cloudinaryService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("getAllTopics")
    public ResponseEntity<?> getAllTopics() {
        List<TopicDto> topics = topicService.getAllTopics();
        Map<String, Object> response = new HashMap<>();
        if(topics.isEmpty()) {
            response.put("message", "No topics found");
            return ResponseEntity.ok(response);

        }
        response.put("topics", topics);
        response.put("message", "Topics returned successfully ");
        return ResponseEntity.ok(response);

    }

    @GetMapping("getTopicById/{topicId}")
    public ResponseEntity<?> getTopicById(@PathVariable("topicId") int id) {
        Map<String, Object> response = new HashMap<>();

        if(id<=0) {
            response.put("message", "No topic id found");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        try{
            TopicDto topicDto=topicService.getTopicById(id);
            response.put("topic", topicDto);
            return ResponseEntity.ok(response);

        }
        catch(Exception e) {
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

    }
    @GetMapping("getAllUserTopics")
    public ResponseEntity<?> getAllUserTopics(@RequestHeader("Authorization") String authorizationHeader) {
        Map<String, Object> response = new HashMap<>();
        String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
        String email = jwtUtil.extractEmail(token); // Get username from token
        if(email==null) {
            response.put("message", "No email found in the token");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            Set<TopicDto> topics = topicService.getAllUserTopics(email);
            response.put("userTopics", topics);
            return ResponseEntity.ok(response);
        }
        catch(Exception e) {
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("getAllActivatedUserTopics")
    public ResponseEntity<?> getAllActivatedUserTopics(@RequestHeader("Authorization") String authorizationHeader) {
        Map<String, Object> response = new HashMap<>();
        String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
        String email = jwtUtil.extractEmail(token); // Get username from token
        if(email==null) {
            response.put("message", "No email found in the token");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            Set<TopicDto> topics = topicService.getAllActivatedUserTopics(email);
            response.put("userTopics", topics);
            return ResponseEntity.ok(response);
        }
        catch(Exception e) {
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("addTopic")
    public ResponseEntity<?> addTopic(@RequestParam("file") MultipartFile file,@RequestParam("name") String name,
                                      @RequestParam("colorRef") String colorRef,@RequestParam("description") String description

                                        ) throws IOException {
        Map<String, Object> response = new HashMap<>();
        if(file.isEmpty()){
            response.put("message", "File is empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        if(name==null  || name.isBlank() ){
            response.put("message", "Name is empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        if(colorRef==null || colorRef.isBlank() ){
            response.put("message", "ColorRef is empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        if(description==null || description.isBlank() ){
            response.put("message", "Description is empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        try {

            Map<String, String> topicObject = new HashMap<>();
            TopicDto savedTopic = topicService.addTopic(file,name,colorRef,description);
            response.put("message", "Topic added successfully");
            topicObject.put("name", savedTopic.getName());
            topicObject.put("id", Integer.toString(savedTopic.getId()));
            topicObject.put("description", savedTopic.getDescription());
            topicObject.put("colorRef", savedTopic.getColorRef());
            topicObject.put("imageUrl", savedTopic.getImageUrl());
            response.put("topic", topicObject);
            return ResponseEntity.ok(response);
        }
        catch (Exception e){
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }

    @PutMapping("updateTopic")
    public ResponseEntity<?> updateTopic(@RequestParam("file") MultipartFile file,@RequestParam("name") String name,
                                         @RequestParam("id") int id,@RequestParam("description") String description,
                                         @RequestParam("colorRef") String colorRef){

        Map<String, Object> response = new HashMap<>();
        if(id<=0 ){
            response.put("message", "id is empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        try {
            Map<String, String> topicObject = new HashMap<>();
            TopicDto savedTopic = topicService.updateTopic(file,id,name,colorRef,description);
            response.put("message", "Topic updated successfully");
            topicObject.put("name", savedTopic.getName());
            topicObject.put("id", Integer.toString(savedTopic.getId()));
            topicObject.put("description", savedTopic.getDescription());
            topicObject.put("colorRef", savedTopic.getColorRef());
            topicObject.put("imageUrl", savedTopic.getImageUrl());
            response.put("topic", topicObject);
            return ResponseEntity.ok(response);

        }
        catch (Exception e){
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            if(file.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File is empty");
            }
            String imageUrl = cloudinaryService.uploadFileToFolder(file,"quizini/topicImages");
            return ResponseEntity.ok(imageUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image");
        }
    }
}

/*
[
        {
        "name": "HTML",
        "colorRef": "E34F26",
        "description": "A markup language used for structuring web pages and applications."
        },
        {
        "name": "CSS",
        "colorRef": "1572B6",
        "description": "A stylesheet language used for describing the presentation of a document written in HTML."
        },
        {
        "name": "C#",
        "colorRef": "68217A",
        "description": "A modern, object-oriented programming language developed by Microsoft for building web applications and software."
        },
        {
        "name": "Python",
        "colorRef": "306998",
        "description": "A high-level, interpreted language known for its readability and simplicity."
        },
        {
        "name": "Java",
        "colorRef": "007396",
        "description": "A versatile and widely used object-oriented programming language used for building various types of applications."
        },
        {
        "name": "Ruby",
        "colorRef": "CC342D",
        "description": "A dynamic, open-source programming language focused on simplicity and productivity."
        },
        {
        "name": "Dart",
        "colorRef": "00B4AB",
        "description": "A client-optimized language for building fast apps for mobile, web, and desktop."
        },
        {
        "name": "Swift",
        "colorRef": "F05138",
        "description": "A powerful and intuitive programming language for macOS, iOS, watchOS, and tvOS applications."
        },
        {
        "name": "Rust",
        "colorRef": "000000",
        "description": "A systems programming language focused on safety, speed, and concurrency."
        },
        {
        "name": "Kotlin",
        "colorRef": "7F52FF",
        "description": "A statically typed programming language that runs on the Java Virtual Machine (JVM)."
        },
        {
        "name": "C++",
        "colorRef": "004482",
        "description": "A high-performance, general-purpose programming language known for its efficiency and control over system resources."
        },
        {
        "name": "C",
        "colorRef": "A8B9CC",
        "description": "A general-purpose programming language used for system and application software."
        },
        {
        "name": "Go",
        "colorRef": "00ADD8",
        "description": "A statically typed, compiled programming language designed for simplicity and efficiency."
        },
        {
        "name": "JavaScript",
        "colorRef": "F7DF1E",
        "description": "A versatile, high-level scripting language that enables interactive web pages and is an essential part of web development."
        }
        ]
*/

