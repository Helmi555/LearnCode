package SGBD_Project.example.LearnCode.Services;

import SGBD_Project.example.LearnCode.Dto.TopicDto;
import SGBD_Project.example.LearnCode.Models.Topic;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

public interface TopicService {
    List<TopicDto> getAllTopics();
    TopicDto addTopic(MultipartFile file,String topicName,String colorRef,String description);
    boolean checkTopicsExistence(Set<Integer> topicsId);
    TopicDto updateTopic(MultipartFile file,int id,String topicName,String colorRef,String description);
    TopicDto getTopicById(int id);
    Set<TopicDto> getAllUserTopics(String email);
    Set<TopicDto> getAllActivatedUserTopics(String email);
}
