package SGBD_Project.example.LearnCode.Services.Impl;

import SGBD_Project.example.LearnCode.Dto.QuestionDto;
import SGBD_Project.example.LearnCode.Models.Question;
import SGBD_Project.example.LearnCode.Models.Topic;
import SGBD_Project.example.LearnCode.Repositories.QuestionRepository;
import SGBD_Project.example.LearnCode.Repositories.TopicRepository;
import SGBD_Project.example.LearnCode.Services.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final TopicRepository topicRepository;

    @Autowired
    public QuestionServiceImpl(QuestionRepository questionRepository,TopicRepository topicRepository) {
        this.questionRepository = questionRepository;
        this.topicRepository=topicRepository;
    }
    @Override
    public QuestionDto createQuestion(QuestionDto questionDto) {

        System.out.println("Ya brooo service start");
        Topic topic=topicRepository.findByName(questionDto.getTopicName());
        if(topic ==null) {
            throw new RuntimeException("This topic doesn't exist"+questionDto.getTopicName());
        }
        System.out.println("topic :"+topic.getName());

        System.out.println("Ya brooo service");
        Question question = new Question();
        question.setQuestion(questionDto.getQuestion());
        question.setDifficulty_level(questionDto.getDifficulty_level());
        question.setHint(questionDto.getHint());
        question.setExplanation(questionDto.getExplanation());
        question.setTopic(topic);
        question.setPropositions(questionDto.getPropositions());
        question.setAnswers(questionDto.getAnswers());


        Question savedQuestion = questionRepository.save(question);
        QuestionDto savedQuestionDto = QuestionDto.builder().build();
        savedQuestionDto.setQuestion(savedQuestion.getQuestion());
        savedQuestionDto.setDifficulty_level(savedQuestion.getDifficulty_level());
        savedQuestionDto.setHint(savedQuestion.getHint());
        savedQuestionDto.setExplanation(savedQuestion.getExplanation());
        savedQuestionDto.setId(savedQuestion.getId());
        savedQuestionDto.setTopicName(topic.getName());
        savedQuestionDto.setPropositions(savedQuestion.getPropositions());
        savedQuestionDto.setAnswers(savedQuestion.getAnswers());
        return savedQuestionDto;
    }

    @Override
    public List<QuestionDto> getAllQuestions() {
        List<Question> questions=questionRepository.findAll();
        List<QuestionDto> questionDtos=new ArrayList<>();
        for(Question question:questions) {
            questionDtos.add(mapToDto(question));
        }
        return questionDtos;

    }

    @Override
    public QuestionDto getQuestionById(String id) {
        try {


            Question question = questionRepository.findById(id).orElse(null);
            if (question == null) {
                throw new RuntimeException("This question doesn't exist : " + id);
            }
            //System.out.println("in the getbyId "+question);
            QuestionDto savedQuestionDto = mapToDto(question);
            return savedQuestionDto;
        }
        catch (Exception e){
             throw new RuntimeException(e.getMessage());
        }
    }


    public QuestionDto mapToDto(Question question) {
        QuestionDto dto = QuestionDto.builder()
                .Id(question.getId())
                .hint(question.getHint())
                .answers(question.getAnswers())
                        .difficulty_level(question.getDifficulty_level())
                                .explanation(question.getExplanation())
                                        .topicName(question.getTopic().getName())
                                                .createdAt(question.getCreatedAt())
                                                        .updatedAt(question.getUpdatedAt())
                                                                .propositions(question.getPropositions())
                                                                        .question(question.getQuestion())
                                                                                .build();
        /*dto.setId(question.getId());
        dto.setQuestion(question.getQuestion());
        dto.setDifficulty_level(question.getDifficulty_level());
        dto.setHint(question.getHint());
        dto.setExplanation(question.getExplanation());
        dto.setTopicName(question.getTopic().getName()); // Assuming Topic has a getName() method
        dto.setPropositions(question.getPropositions());
        dto.setAnswers(question.getAnswers());
        dto.setUpdatedAt(question.getUpdatedAt());
        dto.setCreatedAt(question.getCreatedAt());*/

        return dto;
    }

}
