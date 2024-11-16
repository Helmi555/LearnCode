package SGBD_Project.example.LearnCode.Services;


import SGBD_Project.example.LearnCode.Dto.PostDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

public interface PostService {

    PostDto addPost(PostDto postDto, List<MultipartFile> files);

    Set<PostDto> getNewPosts(String email, Set<Long> postIds, int numberOfPosts);
}
