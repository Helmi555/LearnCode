package SGBD_Project.example.LearnCode.Controllers;

import SGBD_Project.example.LearnCode.Dto.UserEntityDto;
import SGBD_Project.example.LearnCode.Services.FlaskService;
import netscape.javascript.JSObject;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.swing.text.html.HTML;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("api/v1/users")
public class UserController {

    private FlaskService flaskService;
    @Autowired
    public UserController(FlaskService flaskService) {
        this.flaskService = flaskService;
    }


    @PostMapping("/sendToFlask")
    public ResponseEntity<?> sendToFlask(@RequestBody UserEntityDto userEntityDto) {
        // Get user details, questions, and topics from the request
        String userId = userEntityDto.getId();
        Set<Integer> topics = userEntityDto.getTopicsId();
        System.out.println("HIIII controller "+userId +"\n topics id "+topics);

        try {
            List<Map<String, Object>> response = flaskService.sendRequestToFlask(userId, topics);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // Return an error response with a 400 status code
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}





