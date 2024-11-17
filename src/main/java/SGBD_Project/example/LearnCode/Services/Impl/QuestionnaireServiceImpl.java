package SGBD_Project.example.LearnCode.Services.Impl;

import SGBD_Project.example.LearnCode.Dto.QuestionnaireDto;
import SGBD_Project.example.LearnCode.Models.*;
import SGBD_Project.example.LearnCode.Repositories.*;
import SGBD_Project.example.LearnCode.Services.QuestionnaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class QuestionnaireServiceImpl implements QuestionnaireService {

    private final QuestionnaireRepository questionnaireRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final UserQuestionRepository userQuestionRepository;
    private final UserTopicRepository userTopicRepository;


    @Autowired
    public QuestionnaireServiceImpl(QuestionnaireRepository questionnaireRepository, UserRepository userRepository, QuestionRepository questionRepository, UserQuestionRepository userQuestionRepository, UserTopicRepository userTopicRepository) {
        this.questionnaireRepository = questionnaireRepository;
        this.userRepository = userRepository;

        this.questionRepository = questionRepository;
        this.userQuestionRepository = userQuestionRepository;
        this.userTopicRepository = userTopicRepository;
    }


    @Override
    public QuestionnaireDto correctQuestionnaire(String email, Long questionnaireId , List<Map<String, Object>> userAnswers) {
        UserEntity user=userRepository.findByEmail(email).orElse(null);
        if(user==null) {
            throw new RuntimeException("This user doesn't exist : " + email);
        }
        Questionnaire questionnaire=questionnaireRepository.findById(questionnaireId).orElse(null);
        if(questionnaire==null) {
            throw new RuntimeException("This questionnaire doesn't exist : " + questionnaireId);
        }
        if(!Objects.equals(questionnaire.getUser().getId(), user.getId())) {
            throw new RuntimeException("This questionnaire doesn't belongs to userId: " + user.getId());
        }
        if(questionnaire.getNumberOfQuestions()!=userAnswers.size()) {
            throw new RuntimeException("This questionnaire doesn't contain all questionAnswers: " + questionnaire.getNumberOfQuestions()+" "+userAnswers.size());
        }
       /* if(questionnaire.getAnsweredAt()!=null){
            throw new RuntimeException("This questionnaire has already been corrected!");

        }*/
        int numberCorrectAnswers=0;
        Map<Integer,Double> userRanksMap=new HashMap<>();
        for(Map<String, Object> userAnswer:userAnswers) {
            String questionId=(String)userAnswer.get("questionId");
            List<Integer> userAnswersId=(List<Integer>) userAnswer.get("answersId");
            int respondingTime=(Integer) userAnswer.get("respondingTime");
            Question question=questionRepository.findById(questionId).orElse(null);
            if(question==null) {
                throw new RuntimeException("This question doesn't exist : " + questionId);
            }
            if(userAnswersId.isEmpty()){
                throw new RuntimeException("There are no answers for this questions : " + questionId);
            }
            if(respondingTime<0){
                throw new RuntimeException("The responding time cannot be negative");
            }
            List<String> answers=question.getAnswers();
            List<String> propositions=question.getPropositions();
            boolean correct=true;
            for(Integer answerId:userAnswersId) {
                if(!answers.contains(propositions.get(answerId-1))) {
                    correct=false;
                }
            }
            if(correct) numberCorrectAnswers+=1;
            updateUserRankMap(user,question,correct,userRanksMap);
            UserQuestion userQuestion=userQuestionRepository.findByQuestion_IdAndUser_Id(questionId,user.getId());
            if(userQuestion==null) {
                UserQuestion newUserQuestion=UserQuestion.builder()
                        .correctness(correct?1:0)
                        .user(user)
                        .question(question)
                        .given(true)
                        .respondingTime(respondingTime)
                        .build();
                userQuestionRepository.save(newUserQuestion);
            }
            else{
                userQuestion.setCorrectness(correct?1:0);
                userQuestion.setGiven(true);
                userQuestion.setRespondingTime((userQuestion.getRespondingTime()+respondingTime) /2);
                userQuestionRepository.save(userQuestion);
            }
        }
        System.out.println("******** final userRankmap " + userRanksMap);
        userRanksMap.forEach((key,value)->{
            UserTopic userTopic=userTopicRepository.findByUser_IdAndTopic_Id(user.getId(), key);
            if (userTopic == null) {
                throw new RuntimeException("This userTopic doesn't exist with this topicId : " + key);
            }

            // UPDATE userRank *********
            double newRank=userTopic.getRank()+value;
            BigDecimal rankBD= new BigDecimal(newRank).setScale(3, BigDecimal.ROUND_HALF_UP);
            double newRankResult=Math.min(rankBD.doubleValue(),0.9);
            newRankResult=Math.max(newRankResult,0.1);
            userTopic.setRank(newRankResult);

        });
        questionnaire.setCompleted(true);
        questionnaire.setNumberCorrectedAnswers(numberCorrectAnswers);
        questionnaire.setAnsweredAt(LocalDateTime.now());
        questionnaireRepository.save(questionnaire);
        return QuestionnaireDto.toDto(questionnaire);
    }

    @Override
    public List<QuestionnaireDto> getAllQuestionnaires(String email) {
        UserEntity user=userRepository.findByEmail(email).orElse(null);
        if(user==null) {
            throw new RuntimeException("This user doesn't exist : " + email);
        }
        List<Questionnaire> questionnaireList=questionnaireRepository.findAllByUser_Id(user.getId());
        List<QuestionnaireDto> questionnaireDtoList= new ArrayList<>();
        for(Questionnaire questionnaire:questionnaireList) {
            QuestionnaireDto questionnaireDto=QuestionnaireDto.toDto(questionnaire);
            questionnaireDto.setUserId(null);
            questionnaireDto.setQuestions(null);
            questionnaireDtoList.add(questionnaireDto);
        }
        return questionnaireDtoList;

    }

    void updateUserRankMap(UserEntity user, Question question, boolean correct, Map<Integer, Double> userRanksMap) {
        try{
            UserTopic userTopic=userTopicRepository.findByUser_IdAndTopic_Id(user.getId(),question.getTopic().getId());
            double userRank=userTopic.getRank();
            double questionDiffLevel= (double) question.getDifficulty_level() /10;
            double diff=(0.45*(questionDiffLevel-userRank)+0.5-(correct?0:1))/7;
            BigDecimal bd = new BigDecimal(diff).setScale(3, RoundingMode.HALF_UP);
            double doubleResult = bd.doubleValue();

            System.out.println(questionDiffLevel+" - "+userRank+" = "+doubleResult+correct);
            System.out.println("So the old user rank is and the new will be : "+userRank+" --> "+(doubleResult+userRank));

            if(userRanksMap.containsKey(question.getTopic().getId())) {
                double newMapRank=(userRanksMap.get(question.getTopic().getId())+diff)/2;
                BigDecimal newBd = new BigDecimal(newMapRank).setScale(3, RoundingMode.HALF_UP);
                userRanksMap.put(question.getTopic().getId(),newBd.doubleValue());
            }else{
                userRanksMap.put(question.getTopic().getId(),doubleResult);
            }
            System.out.println("+++++++++NEW userMapranks: "+userRanksMap);

        }
        catch(Exception e){
            throw new RuntimeException("Error while updating user rank for questionId: " + question.getId());

        }
    }

}


//   {3=0.087, 4=0.091, 10=0.067, 11=0.127, 12=0.116}