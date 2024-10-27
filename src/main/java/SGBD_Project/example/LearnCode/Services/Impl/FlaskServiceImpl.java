package SGBD_Project.example.LearnCode.Services.Impl;

import SGBD_Project.example.LearnCode.Dto.QuestionDto;
import SGBD_Project.example.LearnCode.Dto.UserQuestionDto;
import SGBD_Project.example.LearnCode.Dto.UserTopicDto;
import SGBD_Project.example.LearnCode.Models.Question;
import SGBD_Project.example.LearnCode.Models.UserEntity;
import SGBD_Project.example.LearnCode.Models.UserQuestion;
import SGBD_Project.example.LearnCode.Models.UserTopic;
import SGBD_Project.example.LearnCode.Repositories.QuestionRepository;
import SGBD_Project.example.LearnCode.Repositories.UserQuestionRepository;
import SGBD_Project.example.LearnCode.Repositories.UserRepository;
import SGBD_Project.example.LearnCode.Repositories.UserTopicRepository;
import SGBD_Project.example.LearnCode.Services.FlaskService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.json.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FlaskServiceImpl implements FlaskService {

    private final UserRepository userRepository;
    private final UserTopicRepository userTopicRepository;
    private final QuestionRepository questionRepository;
    private final UserQuestionRepository userQuestionRepository;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String flaskUrl = "http://localhost:5000/predict"; // Flask endpoint

    @Autowired
    public FlaskServiceImpl (UserRepository userRepository, UserTopicRepository userTopicRepository, QuestionRepository questionRepository,UserQuestionRepository userQuestionRepository) {
        this.userRepository = userRepository;
        this.userTopicRepository = userTopicRepository;
        this.questionRepository = questionRepository;
        this.userQuestionRepository = userQuestionRepository;
    }


    /*@PostConstruct
    @Transactional*/
    public List<Map<String,Object>> sendRequestToFlask(String userId, Set<Integer> topics) {
        UserEntity user=userRepository.findById(userId).orElse(null);
        if(user==null) {
            throw new RuntimeException("User not found with this ID: " + userId);
        }

        Set<Integer> validTopics=userTopicRepository.findUserTopicsByUserId(userId);
        System.out.println("validTopics: "+validTopics);
        for(Integer topic:topics) {
            if(!validTopics.contains(topic)) {
                throw new RuntimeException("Topic not valid: " + topic.toString());
            }
        }
        Set<UserTopic> userTopics=userTopicRepository.findByUser_Id(userId);
        List<List<Object>> userTopicDtos=new ArrayList<>();
        for(UserTopic userTopic:userTopics) {
            List<Object> listTopic=new ArrayList<>();
            listTopic.add(userTopic.getTopic().getId());
            listTopic.add(userTopic.getRank());
            userTopicDtos.add(listTopic);
        }
        List<Question> questionsByTopic=questionRepository.findByTopic_IdIn(new ArrayList<>(topics));

        List<String> questionIds = questionsByTopic.stream()
                .map(Question::getId)
                .collect(Collectors.toList());

        List<UserQuestion> userQuestionsList=userQuestionRepository.findByQuestion_IdIn(questionIds);
        List<List<Object>> userQuestionDtos=new ArrayList<>();
        for(UserQuestion userQuestion:userQuestionsList) {
                /*UserQuestionDto userQuestionDto=UserQuestionDto.builder()
                        .topicId(userQuestion.getQuestion().getTopic().getId())
                        .levelQuestion((double) userQuestion.getQuestion().getDifficulty_level() /10)
                        .given(userQuestion.isGiven())
                        .correctness(userQuestion.getCorrectness())
                        .build();*/
            List<Object> listQuestion=new ArrayList<>();
            listQuestion.add(userQuestion.getQuestion().getTopic().getId() );
            listQuestion.add((double) userQuestion.getQuestion().getDifficulty_level() /10 );
            listQuestion.add(userQuestion.isGiven() ?1:0);
            listQuestion.add(userQuestion.getCorrectness());
            listQuestion.add(userQuestion.getRespondingTime());
            userQuestionDtos.add(listQuestion);
        }

        // Create JSON arrays for "questions" and "topics"
        JSONArray questionsArray = new JSONArray(userQuestionDtos);
        JSONArray topicsArray = new JSONArray(userTopicDtos);

        // Combine everything into the main payload
        JSONObject payload = new JSONObject();
        payload.put("questions", questionsArray);
        payload.put("topics", topicsArray);
        payload.put("questionQuantity",20);
        System.out.println("And the payload is  \n"+payload);
        // Create the JSON payload


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create request houni
        HttpEntity<String> request = new HttpEntity<>(payload.toString(), headers);

        // Send POST request to Flask microservices
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(flaskUrl, request, String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("An error occured " + response.getStatusCode());
            }
            System.out.println("Response: " + response);
            //Parcourir les questions
            List<Map<String,Object>> questionsDtos=new ArrayList<>();
            ObjectMapper objectMapper = new ObjectMapper();
            List<Integer> flaskResponse=objectMapper.readValue(response.getBody(), new TypeReference<>() {
            });
            for (Integer questionIndice:flaskResponse) {
                UserQuestion userQuestion=userQuestionsList.get(questionIndice);
                List<String> propositions=userQuestion.getQuestion().getPropositions();
                //System.out.println("questionId: "+userQuestion.getQuestion().getId()+" : "+ userQuestionsList.get(questionIndice).getQuestion().getQuestion()+"\n propositions: "+propositions);
                Map<String, Object> questionMap = new HashMap<>();
                Map<Integer,String> prop=new HashMap<>();
                prop.put(1,!propositions.get(0).isBlank()?propositions.get(0):"");
                prop.put(2,!propositions.get(1).isBlank()?propositions.get(1):"");
                prop.put(3,!propositions.get(2).isBlank()?propositions.get(2):"");
                prop.put(4,!propositions.get(3).isBlank()?propositions.get(3):"");
                questionMap.put("question", userQuestionsList.get(questionIndice).getQuestion().getQuestion());
                questionMap.put("propositions", prop);

                questionsDtos.add(questionMap);
            }
            return questionsDtos;

        } catch (RestClientException | JsonProcessingException e) {
            System.out.println("Exception occurred: " + e.getMessage());
            // Return an empty list in case of an exception
            return new ArrayList<>();
        }

    }

}


