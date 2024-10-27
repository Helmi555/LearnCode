package SGBD_Project.example.LearnCode.Services;

import org.json.JSONObject;

import java.util.Set;

public interface FlaskService {

    JSONObject sendRequestToFlask(String userDetails, Set<Integer> topics);
}
