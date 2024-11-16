package SGBD_Project.example.LearnCode.Controllers;

import SGBD_Project.example.LearnCode.Security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import SGBD_Project.example.LearnCode.Dto.PostDto;
import SGBD_Project.example.LearnCode.Models.Enums.Type;
import SGBD_Project.example.LearnCode.Models.Post;
import SGBD_Project.example.LearnCode.Services.PostService;
import org.apache.commons.lang3.EnumUtils;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.*;

@Controller
@RequestMapping("/api/v1/posts/")

public class PostController {

    private final PostService postService;
    private final JwtUtil jwtUtil;

    public PostController(PostService postService, JwtUtil jwtUtil) {
        this.postService = postService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("addPost")
    public ResponseEntity<?> addPost(@ModelAttribute PostDto postDto,@RequestPart("files") List<MultipartFile> files  ) {
        Map<String,Object> msg = new HashMap<>();
        if(postDto==null){
            msg.put("message","No Post founded");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }
        if(postDto.getAuthor().isBlank()){
            msg.put("message","No Author founded");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }
        if(postDto.getTitle().isBlank()){
            msg.put("message","No Title founded");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }
        if(postDto.getContent().isBlank()){
            msg.put("message","No Content founded");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }

        if (postDto.getDate()==null){
            msg.put("message","No Date founded");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }
        if(postDto.getTags().isEmpty()){
            msg.put("message","No Tags founded");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }

        if(postDto.getType().isBlank() ||  !EnumUtils.isValidEnum(Type.class, postDto.getType()))
        {
            msg.put("message","No Type founded/Correct");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }
        if(postDto.getLinks()==null){
            msg.put("message","No Links attribute founded");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }
        if(files==null ){
            msg.put("message","No Files field founded");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }

        System.out.println("tags are: "+postDto.getTags());
        //ObjectMapper objectMapper = new ObjectMapper();
        //postDto.setTags(objectMapper.readValue(postDto.getTags(), new TypeReference<List<String>>() {}));

        try{
            if (files != null && !files.isEmpty()) {
                files.removeIf(file -> file.isEmpty());
                System.out.println("Files are " + files + " with size " + files.size());
            }
            PostDto newPostDto = postService.addPost(postDto,files);
            msg.put("message","Successfully Added Post");
            msg.put("post",newPostDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(msg);

        }
        catch(Exception e){
            msg.put("message",e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(msg);
        }

    }

    @PostMapping("getNewPosts")
    public ResponseEntity<?> getNewPosts(@RequestBody Map<String,Object> requestBody,@RequestHeader("Authorization") String authorizationHeader){
        Map<String,Object> msg = new HashMap<>();
        String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
        String email = jwtUtil.extractEmail(token);
        if (email == null || email.isEmpty()) {
            msg.put("message","No email found");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }
        if(requestBody==null){
            msg.put("message","No Fields founded");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }
        List<?> postIdsList = (List<?>) requestBody.get("cachedPostsId");
        Set<Long> postIds = new HashSet<>();
        for (Object id : postIdsList) {
            if (id instanceof Integer) {
                postIds.add(((Integer) id).longValue());
            } else if (id instanceof Long) {
                postIds.add((Long) id);
            } else {
                msg.put("message", "Invalid Post ID type");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
            }
        }
        System.out.println("the postIdsList : "+postIds);

        int numberOfPosts=(int)requestBody.get("numberOfPosts");
        if(numberOfPosts<=0){
            msg.put("message","No Posts wanted");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }
        try{
            Set<PostDto> newPosts=postService.getNewPosts(email,postIds,numberOfPosts);
            msg.put("message","Successfully Retrieved Posts");
            msg.put("posts",newPosts);
            return ResponseEntity.status(HttpStatus.OK).body(msg);
        }
        catch(Exception e){
            msg.put("message",e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(msg);
        }

    }

    @PostMapping("upvotePostById/{postId}")
    public ResponseEntity<?> upvotePostById(@PathVariable Long postId,@RequestHeader("Authorization") String authorizationHeader){
        Map<String,Object> msg = new HashMap<>();
        String token = authorizationHeader.substring(7);
        String email = jwtUtil.extractEmail(token);
        if (email == null || email.isEmpty()) {
            msg.put("message","No email found");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }
        if(postId==null){
            msg.put("message","No Post founded for up voting it");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }
        try{
            Map<String,Object> postDto =postService.upvotePostById(email,postId);
            msg.put("message","Successfully Upvoted Post");
            msg.put("post",postDto);
            return ResponseEntity.status(HttpStatus.OK).body(msg);

        }
        catch(Exception e){
            msg.put("message",e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(msg);
        }
    }

    @PostMapping("downvotePostById/{postId}")
    public ResponseEntity<?> downvotePostById(@PathVariable Long postId,@RequestHeader("Authorization") String authorizationHeader){
        Map<String,Object> msg = new HashMap<>();
        String token = authorizationHeader.substring(7);
        String email = jwtUtil.extractEmail(token);
        if (email == null || email.isEmpty()) {
            msg.put("message","No email found");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }
        if(postId==null){
            msg.put("message","No Post founded for up voting it");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }
        try{
            Map<String,Object> postDto =postService.downvotePostById(email,postId);
            msg.put("message","Successfully downVoted Post");
            msg.put("post",postDto);
            return ResponseEntity.status(HttpStatus.OK).body(msg);

        }
        catch(Exception e){
            msg.put("message",e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(msg);
        }
    }
}
