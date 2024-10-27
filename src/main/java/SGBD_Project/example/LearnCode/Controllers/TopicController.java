package SGBD_Project.example.LearnCode.Controllers;

import SGBD_Project.example.LearnCode.Dto.TopicDto;
import SGBD_Project.example.LearnCode.Models.Topic;
import SGBD_Project.example.LearnCode.Services.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/v1/topics/")
public class TopicController {

    private TopicService topicService;

    @Autowired
    public TopicController(TopicService topicService) {
        this.topicService = topicService;
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
    @PostMapping("addTopic")
    public ResponseEntity<?> addTopic(@RequestBody TopicDto topicDto) {
        Map<String, Object> response = new HashMap<>();
        if(topicDto.getName().isEmpty()){
            response.put("message", "Topic name cannot be empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        Map<String,String> topicObject= new HashMap<>();
        TopicDto savedTopic= topicService.addTopic(topicDto);
        response.put("message", "Topic added successfully");
        topicObject.put("name", savedTopic.getName());
        topicObject.put("id",Integer.toString(savedTopic.getId()));
        response.put("topic", topicObject);
        return ResponseEntity.ok(response);

    }
}
