package SGBD_Project.example.LearnCode.Services.Impl;

import SGBD_Project.example.LearnCode.Dto.QuestionDto;
import SGBD_Project.example.LearnCode.Dto.UserEntityDto;
import SGBD_Project.example.LearnCode.Models.*;
import SGBD_Project.example.LearnCode.Repositories.*;
import SGBD_Project.example.LearnCode.Security.JwtUtil;
import SGBD_Project.example.LearnCode.Security.SecurityConfig;
import SGBD_Project.example.LearnCode.Services.QuestionService;
import SGBD_Project.example.LearnCode.Services.UserService;
import SGBD_Project.example.LearnCode.Utils.IdGenerator;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    QuestionRepository questionRepository;
    TopicRepository topicRepository;
    UserRepository userRepository;
    UserTopicRepository userTopicRepository;
    UserQuestionRepository userQuestionRepository;
    JwtUtil jwtUtil;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, TopicRepository topicRepository, UserTopicRepository userTopicRepository, QuestionRepository questionRepository, UserQuestionRepository userQuestionRepository, JwtUtil jwtUtil, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.topicRepository = topicRepository;
        this.userTopicRepository = userTopicRepository;
        this.questionRepository = questionRepository;
        this.userQuestionRepository = userQuestionRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserEntityDto createUser(UserEntityDto userDto) {
        try {
            // Check if the user already exists
            Optional<UserEntity> userEntity = userRepository.findByEmail(userDto.getEmail());
            if (userEntity.isPresent()) {
                throw new Exception("User already exists");
            }

            // Encode the password
            String encodedPassword = passwordEncoder.encode(userDto.getPassword());

            // Fetch topics
            Set<Topic> topics = topicRepository.findByIdIn(userDto.getTopicsId());
            String generatedId = IdGenerator.generateId();

            // Create UserEntity
            UserEntity savingUser = UserEntity.builder()
                    .id(generatedId)
                    .age(userDto.getAge())
                    .name(userDto.getName())
                    .password(encodedPassword)
                    .lastName(userDto.getLastName())
                    .address(userDto.getAddress())
                    .email(userDto.getEmail())
                    .birthDay(userDto.getBirthDay())
                    .isBanned(false)
                    .build();

            // Save the UserEntity first to get a valid user ID
            UserEntity savedUser = userRepository.save(savingUser);

            // Now that the user is saved, create UserTopic entities with a valid user reference
            Set<UserTopic> userTopics = new HashSet<>();
            for (Topic topic : topics) {
                UserTopic userTopic = UserTopic.builder()
                        .user(savedUser) // Now we can set the user since it's saved
                        .topic(topic)
                        .rank(0.0) // Default rank
                        .build();

                userTopics.add(userTopic);
            }

            // Save the UserTopic entities
            userTopicRepository.saveAll(userTopics);
            savedUser.setUserTopics(userTopics);
            System.out.println("aaaaaaaaaaa");
            //userRepository.save(savedUser);

            return mapToDto(savedUser);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create user. " + e.getMessage());
        }
    }

    @Override
    public UserEntityDto getUserById(String userDtoId) {
        try {

            UserEntity user = userRepository.findById(userDtoId).orElse(null);
            if (user == null) {
               throw new Exception("User not found");
            }
            String userId=user.getId();
            UserEntityDto userDto = mapToDto(user);
            //userDto.setUserTopics(userTopicRepository.findByUserId(userId));
            Set<Integer> userTopics=userTopicRepository.findUserTopicsByUserId(userId);
            Set<String>topics=new HashSet<>();
            for(int userTopic:userTopics){
                Topic topic=topicRepository.findById(userTopic).orElse(null);
                if(topic!=null ){
                    topics.add(topic.getName());
                }
            }
            userDto.setTopicsName(topics);
            return userDto;

        }
        catch (Exception e) {
            throw new RuntimeException("Failed to get user. " + e.getMessage());
        }
    }

    @Override
    public void addQuesToUser(String userId) {
        UserEntity user=userRepository.findById(userId).orElse(null);
        if(user==null){
            throw new RuntimeException("User not found");
        }
        Set<UserTopic> userTopics=userTopicRepository.findByUser_Id(userId);
        if(userTopics==null || userTopics.isEmpty()){
            throw new RuntimeException("This user has no topics");
        }
        List<Integer> indices= Arrays.asList(9,11,12,14);
        Random random=new Random();
        for(UserTopic userTopic:userTopics){
                int indice=getRandomIndex(indices);
                int topicId=userTopic.getTopic().getId();
            List<Question> questionsByTopic=questionRepository.findByTopic_Id(topicId);
            System.out.println("topic id : "+topicId+" there are "+questionsByTopic.size()+" questions "+" indice : "+indice);
            for (int i = 0; i <indice; i++) {
                UserQuestion userQuestion=UserQuestion.builder()
                        .given(true)
                        .correctness(random.nextInt(2)*1.0)
                        .respondingTime(random.nextInt(120)+10)
                        .question(questionsByTopic.get(i))
                        .user(user)
                        .build();
                UserQuestion newUserQuestion=userQuestionRepository.save(userQuestion);
                System.out.println("New user question id "+newUserQuestion.getId());

            }
        }
    }

    @Override
    public String signIn(String email, String password) {
        try {
            UserEntity user = userRepository.findByEmail(email).orElse(null);
            if (user == null) {
                throw new RuntimeException("User not found");
            }
            boolean matches = BCrypt.checkpw(password, user.getPassword());
            if (!matches) {
                throw new RuntimeException("Wrong password");
            }
            String token= jwtUtil.generateToken(email);
            user.setLastLoginDate(LocalDateTime.now());
            user.addToken(token);
            userRepository.save(user);
            return token;

        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    @Override
    public Boolean signOut(String token) {
            String email=jwtUtil.extractEmail(token);
            UserEntity user=userRepository.findByEmail(email).orElse(null);
            if(user==null){
                throw new RuntimeException("User not found");
            }

            List<String> tokens=user.getTokens();
            if(tokens==null || tokens.isEmpty() || !tokens.contains(token)){
                return false;
            }
            System.out.println("user tokens :"+tokens);
            user.removeToken(token);
            userRepository.save(user);
            return true;
    }

    @Override
    public void saveSelectedTopics(String email, Set<Integer> topicsId) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        for (Integer topicId : topicsId) {
            Topic topic = topicRepository.findById(topicId)
                    .orElseThrow(() -> new RuntimeException("Topic " + topicId + " not found"));

            UserTopic userTopic = UserTopic.builder()
                    .user(user)
                    .topic(topic)
                    .rank(0.0)
                    .build();

            user.addUserTopic(userTopic);
        }

        try {
            userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save user topics: " + e.getMessage());
        }
    }

    @Override
    public void updateUserRanks(String email, List<Map<String, Object>> topics) {
        UserEntity user = userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("User "+email+" not found"));
        for (Map<String, Object> topicData : topics) {
            Integer topicId = (Integer) topicData.get("topicId");
            Integer rank = (Integer) topicData.get("rank");
            if(rank<1 || rank >4){
                throw new RuntimeException("Rank out of range");
            }

            Topic topic = topicRepository.findById(topicId)
                    .orElseThrow(() -> new RuntimeException("Topic " + topicId + " not found"));

            UserTopic userTopic = userTopicRepository.findByUser_IdAndTopic_Id(user.getId(),topicId);
            if(userTopic==null){
                throw new RuntimeException("UserTopic not found for topic id "+topicId);
            }
            userTopic.setRank(UserTopic.getRankFromLevel(rank));

            userTopicRepository.save(userTopic);
        }
    }

    public static int getRandomIndex(List<Integer> indices) {
        Random random = new Random();
        int randomIndex = random.nextInt(indices.size());  // Get a random index within the list size
        return indices.get(randomIndex);  // Return the element at the random index
    }
    /* @PostConstruct
     @Transactional*/
    public void updateUserTopic() {
        System.out.println("Asleeeemaaaaaaaaaaaaaa");
        try {
            List<UserTopic> userTopics = userTopicRepository.findAll();
            for (UserTopic userTopic : userTopics) {
                double rank;
                DecimalFormat df=new DecimalFormat("#.#");
                rank= Double.parseDouble(df.format(Math.random()*0.8));
                userTopic.setRank(rank);
                userTopicRepository.save(userTopic);
            }
        }catch (Exception e) {
            System.out.println("probelllllllm");
            e.printStackTrace();
        }
    }

    public UserEntityDto mapToDto(UserEntity userEntity) {
        return UserEntityDto.builder()
                .id(userEntity.getId())
                .age(userEntity.getAge())
                .name(userEntity.getName())
                .lastName(userEntity.getLastName())
                .address(userEntity.getAddress())
                .email(userEntity.getEmail())
                .birthDay(userEntity.getBirthDay())
                .isBanned(userEntity.getIsBanned())
                .createdDate(userEntity.getCreatedDate())
                .updatedDate(userEntity.getUpdatedDate())
                .build();
    }
}
