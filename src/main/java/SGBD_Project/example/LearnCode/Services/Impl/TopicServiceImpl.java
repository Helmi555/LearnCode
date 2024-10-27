package SGBD_Project.example.LearnCode.Services.Impl;

import SGBD_Project.example.LearnCode.Dto.TopicDto;
import SGBD_Project.example.LearnCode.Models.Topic;
import SGBD_Project.example.LearnCode.Repositories.TopicRepository;
import SGBD_Project.example.LearnCode.Services.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TopicServiceImpl implements TopicService {

    private TopicRepository topicRepository;

    @Autowired
    public TopicServiceImpl(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }
    @Override
    public List<TopicDto> getAllTopics() {
        List<Topic> topics = topicRepository.findAll();
        return topics.stream().map(this::MapToDto).collect(Collectors.toList());
    }

    @Override
    public TopicDto addTopic(TopicDto topicDto) {
        Topic topic = new Topic();
        topic.setName(topicDto.getName());
        Topic saveTopic = topicRepository.save(topic);
        TopicDto savedTopicDto = new TopicDto();
        savedTopicDto.setId(saveTopic.getId());
        savedTopicDto.setName(saveTopic.getName());
        return savedTopicDto;
    }

    @Override
    public boolean checkTopicsExistence(Set<Integer> topicsIdList) {
        for (Integer topicId : topicsIdList) {
            Optional<Topic> topic = topicRepository.findById(topicId);
            if(topic.isEmpty()){
                return false;
            }
        }
        return true;
    }

    public TopicDto MapToDto(Topic topic) {
        TopicDto topicDto = new TopicDto();
        topicDto.setId(topic.getId());
        topicDto.setName(topic.getName());
        return topicDto;
    }
}
