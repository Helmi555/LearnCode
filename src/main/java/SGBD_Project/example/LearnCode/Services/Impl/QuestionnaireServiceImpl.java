package SGBD_Project.example.LearnCode.Services.Impl;

import SGBD_Project.example.LearnCode.Dto.QuestionnaireDto;
import SGBD_Project.example.LearnCode.Models.Question;
import SGBD_Project.example.LearnCode.Models.Questionnaire;
import SGBD_Project.example.LearnCode.Models.UserEntity;
import SGBD_Project.example.LearnCode.Models.UserQuestion;
import SGBD_Project.example.LearnCode.Repositories.QuestionRepository;
import SGBD_Project.example.LearnCode.Repositories.QuestionnaireRepository;
import SGBD_Project.example.LearnCode.Repositories.UserQuestionRepository;
import SGBD_Project.example.LearnCode.Repositories.UserRepository;
import SGBD_Project.example.LearnCode.Services.QuestionnaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class QuestionnaireServiceImpl implements QuestionnaireService {

    private final QuestionnaireRepository questionnaireRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final UserQuestionRepository userQuestionRepository;


    @Autowired
    public QuestionnaireServiceImpl(QuestionnaireRepository questionnaireRepository, UserRepository userRepository, QuestionRepository questionRepository, UserQuestionRepository userQuestionRepository) {
        this.questionnaireRepository = questionnaireRepository;
        this.userRepository = userRepository;

        this.questionRepository = questionRepository;
        this.userQuestionRepository = userQuestionRepository;
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
        int numberCorrectAnswers=0;
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

        questionnaire.setCompleted(true);
        questionnaire.setNumberCorrectedAnswers(numberCorrectAnswers);
        questionnaire.setAnsweredAt(LocalDateTime.now());
        questionnaireRepository.save(questionnaire);
        return QuestionnaireDto.toDto(questionnaire);
    }

}
