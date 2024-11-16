package SGBD_Project.example.LearnCode.Services.Impl;

import SGBD_Project.example.LearnCode.Dto.QuestionDto;
import SGBD_Project.example.LearnCode.Models.*;
import SGBD_Project.example.LearnCode.Repositories.*;
import SGBD_Project.example.LearnCode.Services.FlaskService;
import SGBD_Project.example.LearnCode.Services.QuestionnaireService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final QuestionnaireRepository questionnaireRepository;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String flaskUrl = "http://localhost:5000/predict"; // Flask endpoint

    @Autowired
    public FlaskServiceImpl (UserRepository userRepository, UserTopicRepository userTopicRepository, QuestionRepository questionRepository, UserQuestionRepository userQuestionRepository, QuestionnaireRepository questionnaireRepository) {
        this.userRepository = userRepository;
        this.userTopicRepository = userTopicRepository;
        this.questionRepository = questionRepository;
        this.userQuestionRepository = userQuestionRepository;
        this.questionnaireRepository = questionnaireRepository;
    }

    /*@PostConstruct*/
    @Transactional
    public Map<String,Object> sendRequestToFlask(String email, Set<Integer> topics, Integer questionQuantity) {
        UserEntity user=userRepository.findByEmail(email).orElse(null);
        if(user==null) {
            throw new RuntimeException("User not found with this ID: " + email);
        }
        String userId=user.getId();
        Set<Integer> validTopics=userTopicRepository.findUserTopicsByUserId(userId);
        System.out.println("validTopics: "+validTopics);
        for(Integer topic:topics) {
            if(!validTopics.contains(topic)) {
                throw new RuntimeException("Topic not valid for this user: " + topic.toString());
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
        //System.out.println("*********** questionIds : " + questionIds);
        List<UserQuestion> userQuestionsList=userQuestionRepository.findByQuestion_IdIn(questionIds);
        //System.out.println("************ userQuestionsList : " + userQuestionsList);

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
        System.out.println("questionsArray: "+questionsArray);
        System.out.println("topicsArray: "+topicsArray);
        System.out.println("questionQuantity: "+questionQuantity);
        payload.put("questions", questionsArray);
        payload.put("topics", topicsArray);
        payload.put("questionQuantity",questionQuantity+10);
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
            Set<Question> questionSet=new HashSet<>();
            List<Integer> flaskResponse=objectMapper.readValue(response.getBody(), new TypeReference<>() {});

            //return null;

            for (Integer questionIndice:flaskResponse) {
                UserQuestion userQuestion=userQuestionsList.get(questionIndice);
                Question question=userQuestionsList.get(questionIndice).getQuestion();
                System.out.println("******* flaskIndice: "+questionIndice +" la question est : "+ question);
                questionSet.add(question);
                List<String> propositions=userQuestion.getQuestion().getPropositions();
                //System.out.println("questionId: "+userQuestion.getQuestion().getId()+" : "+ userQuestionsList.get(questionIndice).getQuestion().getQuestion()+"\n propositions: "+propositions);
                Map<Integer,String> props=new HashMap<>();
                for (int i = 0; i < propositions.size(); i++) {
                    props.put(i+1,propositions.get(i));
                }

                Map<String,Object> questionDtoMap=QuestionDto.toMapDto(question,props);
                questionsDtos.add(questionDtoMap);
            }

            // save the questionnaire

            Questionnaire questionnaire=Questionnaire.builder()
                    .questions(questionSet)
                    .completed(false)
                    .numberOfQuestions(questionSet.size())
                    .numberCorrectedAnswers(-1)
                    .description("")
                    .build();
            Questionnaire savedQuestionnaire=questionnaireRepository.save(questionnaire);
            Long questionnaireId=savedQuestionnaire.getId();
            Set<Questionnaire> userQuestionnaireSet=user.getQuestionnaires();
            userQuestionnaireSet.add(savedQuestionnaire);
            savedQuestionnaire.setUser(user);
            userRepository.save(user);
            Map<String,Object> questionnaireResult=new HashMap<>();
            questionnaireResult.put("questionnaireId",questionnaireId);
            questionnaireResult.put("questions",questionsDtos);
            return questionnaireResult;


        } catch (RestClientException | JsonProcessingException e) {
            System.out.println("Exception occurred: " + e.getMessage());
            // Return an empty list in case of an exception
            return new HashMap<>();
        }

    }

}


