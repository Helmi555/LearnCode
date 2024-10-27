package SGBD_Project.example.LearnCode.Services;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface FlaskService {

    List<Map<String,Object>> sendRequestToFlask(String userDetails, Set<Integer> topics);
}
