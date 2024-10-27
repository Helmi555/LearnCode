package SGBD_Project.example.LearnCode.Services;

import SGBD_Project.example.LearnCode.Dto.TopicDto;
import SGBD_Project.example.LearnCode.Models.Topic;

import java.util.List;
import java.util.Set;

public interface TopicService {
    List<TopicDto> getAllTopics();
    TopicDto addTopic(TopicDto topicDto);
    boolean checkTopicsExistence(Set<Integer> topicsId);
}
