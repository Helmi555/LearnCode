package SGBD_Project.example.LearnCode.Controllers;

import SGBD_Project.example.LearnCode.Dto.QuestionDto;
import SGBD_Project.example.LearnCode.Services.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/v1/questions/")
public class QuestionController {

    private QuestionService questionService;
    @Autowired
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping("")
    public String home(){
        return "redirect:/questions/";
    }

    @PostMapping("addQuestion")
    public ResponseEntity<?> addQuestion(@RequestBody QuestionDto questionDto) {


        //return ResponseEntity.status(200).body(questionDto.getPropositions()+ Boolean.toString(questionDto.isValidPropositions())+questionDto.getAnswers()+Boolean.toString(questionDto.isValidAnswers()));
        Map<String,Object> msg = new HashMap<>();
        if(questionDto.getExplanation() == null || questionDto.getExplanation().isBlank() ||
                questionDto.getQuestion() == null || questionDto.getQuestion().isBlank() ||
                questionDto.getHint() == null || questionDto.getHint().isBlank() ||
                questionDto.getDifficulty_level() < 0 ||
                questionDto.getTopicName() == null || questionDto.getTopicName().isBlank()||
                !questionDto.isValidAnswers() || !questionDto.isValidPropositions()
                    ){
            msg.put("message","Please fill all the fields");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }

        try {
            System.out.println("Ya brooo  Question controller");
            Map<String, String> questionObject = new HashMap<>();
            QuestionDto savedQuestion = questionService.createQuestion(questionDto);
            questionObject.put("explanation", savedQuestion.getExplanation());
            questionObject.put("question", savedQuestion.getQuestion());
            questionObject.put("difficulty_level", Integer.toString(savedQuestion.getDifficulty_level()));
            questionObject.put("id", savedQuestion.getId());
            questionObject.put("hint", savedQuestion.getHint());
            questionObject.put("topic", savedQuestion.getTopicName());
            msg.put("message", "Question created successfully");
            msg.put("questionObject", questionObject);
            System.out.println("question created sucessfully" + questionObject);
            return ResponseEntity.status(HttpStatus.CREATED).body(msg);
        }catch (Exception e){
            msg.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(msg);
        }

    }

    @GetMapping("getQuestionById/{questionId}")
    public ResponseEntity<?> getQuestionById(@PathVariable("questionId") String id) {
        Map<String,Object> msg = new HashMap<>();
        if(id == null || id.isBlank()){
            msg.put("message","Please enter the requestId");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }
        try {
            QuestionDto questionDto = questionService.getQuestionById(id);
            msg.put("message","Question returned successfully");
            msg.put("question", questionDto);
            return ResponseEntity.status(HttpStatus.OK).body(msg);
        }
        catch (Exception e){
            msg.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(msg);
        }
    }
    @GetMapping("getAllQuestions")
    public ResponseEntity<?> getAllQuestions() {
        Map<String,Object> msg = new HashMap<>();
        List<QuestionDto> questions=questionService.getAllQuestions();
        msg.put("message","All questions returned successfully");
        msg.put("questions", questions);
        return ResponseEntity.status(HttpStatus.OK).body(msg);
    }

    @PostMapping("addListOfQuestions")
    public ResponseEntity<?> addListOfQuestions(@RequestBody List<QuestionDto> list){
        Map<String,Object> msg = new HashMap<>();
        if(list == null || list.isEmpty()){
            msg.put("message","Please enter the list of questions");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }
        List<QuestionDto> questions=new ArrayList<>();
        for(QuestionDto questionDto : list){
            QuestionDto question = questionService.createQuestion(questionDto);
            questions.add(question);
        }
        msg.put("message","Question created successfully");
        msg.put("questions", questions);
        return ResponseEntity.status(HttpStatus.CREATED).body(msg);
    }
}
