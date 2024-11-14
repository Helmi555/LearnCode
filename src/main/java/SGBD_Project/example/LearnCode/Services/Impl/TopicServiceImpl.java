package SGBD_Project.example.LearnCode.Services.Impl;

import SGBD_Project.example.LearnCode.Dto.TopicDto;
import SGBD_Project.example.LearnCode.Models.Topic;
import SGBD_Project.example.LearnCode.Models.UserEntity;
import SGBD_Project.example.LearnCode.Models.UserTopic;
import SGBD_Project.example.LearnCode.Repositories.TopicRepository;
import SGBD_Project.example.LearnCode.Repositories.UserRepository;
import SGBD_Project.example.LearnCode.Repositories.UserTopicRepository;
import SGBD_Project.example.LearnCode.Services.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;
    private final CloudinaryService cloudinaryService;
    private final UserRepository userRepository;
    private final UserTopicRepository userTopicRepository;

    @Autowired
    public TopicServiceImpl(TopicRepository topicRepository, CloudinaryService cloudinaryService, UserRepository userRepository, UserTopicRepository userTopicRepository) {
        this.topicRepository = topicRepository;
        this.cloudinaryService = cloudinaryService;
        this.userRepository = userRepository;
        this.userTopicRepository = userTopicRepository;
    }
    @Override
    public List<TopicDto> getAllTopics() {
        List<Topic> topics = topicRepository.findAll();
        return topics.stream().map(this::MapToDto).collect(Collectors.toList());
    }

    @Override
    public TopicDto addTopic(MultipartFile file,String name,String colorRef,String description) {

        if(topicRepository.findByName(name)!=null){
            throw new RuntimeException("Topic with this name already exists");
        }

        Topic topic = new Topic();
        topic.setName(name);
        topic.setDescription(description);
        topic.setColorRef(colorRef);
        Topic saveTopic = topicRepository.save(topic);

        //upload the image
        String imageUrl;
        try {
            imageUrl=cloudinaryService.uploadFileToFolder(file,"quizini/topicImages/"+saveTopic.getId());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        topic.setImageUrl(imageUrl);
        topicRepository.save(topic);
        TopicDto savedTopicDto = new TopicDto();
        savedTopicDto.setId(saveTopic.getId());
        savedTopicDto.setName(saveTopic.getName());
        savedTopicDto.setDescription(saveTopic.getDescription());
        savedTopicDto.setColorRef(saveTopic.getColorRef());
        savedTopicDto.setImageUrl(saveTopic.getImageUrl());
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

    @Override
    public TopicDto updateTopic(MultipartFile file, int id, String topicName, String colorRef, String description) {
        Topic savedTopic=topicRepository.findById(id).orElse(null);
        if(savedTopic==null){
            throw new RuntimeException("Topic with this id does not exist");
        }
        if(!topicName.isBlank()){
            savedTopic.setName(topicName);
        }
        if(!colorRef.isBlank()){
            savedTopic.setColorRef(colorRef);

        }
        if(!description.isBlank()){
            savedTopic.setDescription(description);
        }
        if(!file.isEmpty()){
            String imageUrl;
            try {
                imageUrl=cloudinaryService.uploadFileToFolder(file,"quizini/topicImages/"+id);
                savedTopic.setImageUrl(imageUrl);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Topic saveTopic=topicRepository.save(savedTopic);
        TopicDto savedTopicDto = new TopicDto();
        savedTopicDto.setId(saveTopic.getId());
        savedTopicDto.setName(saveTopic.getName());
        savedTopicDto.setDescription(saveTopic.getDescription());
        savedTopicDto.setColorRef(saveTopic.getColorRef());
        savedTopicDto.setImageUrl(saveTopic.getImageUrl());
        return savedTopicDto;

    }

    @Override
    public TopicDto getTopicById(int id) {
        Topic topic=topicRepository.findById(id).orElse(null);
        if(topic==null){
            throw new RuntimeException("Topic with this id does not exist");
        }
        return MapToDto(topic);
    }

    @Override
    public Set<TopicDto> getAllUserTopics(String email) {
        try {
            UserEntity user = userRepository.findByEmail(email).orElse(null);
            if (user == null) {
                throw new RuntimeException("User with this email does not exist");
            }
            Set<UserTopic> userTopics = user.getUserTopics();
            Set<TopicDto> topicDtoSet = new HashSet<>();
            for (UserTopic userTopic : userTopics) {
                Topic topic = topicRepository.findById(userTopic.getTopic().getId()).orElse(null);
                if (topic == null) {
                    throw new RuntimeException("Topic with this id does not exist: " + userTopic.getTopic().getId());
                }
                TopicDto topicDto = MapToDto(topic);
                topicDto.setUserRank(userTopic.getRank());
                topicDtoSet.add(topicDto);

            }
            return topicDtoSet;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<TopicDto> getAllActivatedUserTopics(String email) {
        UserEntity user=userRepository.findByEmail(email).orElse(null);
        if(user==null){
            throw new RuntimeException("User with this email does not exist");
        }
        Set<UserTopic> userTopics=user.getUserTopics();
        Set<TopicDto> topicDtoSet=new HashSet<>();
        for(UserTopic userTopic:userTopics){
            Topic topic=topicRepository.findById(userTopic.getTopic().getId()).orElse(null);
            if(topic==null){
                throw new RuntimeException("Topic with this id does not exist: "+userTopic.getTopic().getId());
            }
            if(userTopic.getActivated()){
                TopicDto topicDto = MapToDto(topic);
                topicDto.setUserRank(userTopic.getRank());
                topicDtoSet.add(topicDto);

            }

        }
        return topicDtoSet;
    }

    public TopicDto MapToDto(Topic topic) {
        TopicDto topicDto = new TopicDto();
        topicDto.setId(topic.getId());
        topicDto.setName(topic.getName());
        topicDto.setDescription(topic.getDescription());
        topicDto.setImageUrl(topic.getImageUrl());
        topicDto.setColorRef(topic.getColorRef());
        return topicDto;
    }
}
