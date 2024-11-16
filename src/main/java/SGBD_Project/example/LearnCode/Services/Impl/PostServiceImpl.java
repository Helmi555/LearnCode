package SGBD_Project.example.LearnCode.Services.Impl;

import SGBD_Project.example.LearnCode.Dto.PostDto;
import SGBD_Project.example.LearnCode.Models.*;
import SGBD_Project.example.LearnCode.Models.Enums.Type;
import SGBD_Project.example.LearnCode.Repositories.*;
import SGBD_Project.example.LearnCode.Services.PostService;
import SGBD_Project.example.LearnCode.Utils.PostUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final CloudinaryService cloudinaryService;
    private final UserRepository userRepository;
    private final UserTopicRepository userTopicRepository;
    private final TopicRepository topicRepository;
    private final PostUserRepository postUserRepository;

    public PostServiceImpl(PostRepository postRepository, CloudinaryService cloudinaryService, UserRepository userRepository, UserTopicRepository userTopicRepository, TopicRepository topicRepository, PostUserRepository postUserRepository) {
        this.postRepository = postRepository;
        this.cloudinaryService = cloudinaryService;
        this.userRepository = userRepository;
        this.userTopicRepository = userTopicRepository;
        this.topicRepository = topicRepository;
        this.postUserRepository = postUserRepository;
    }


    @Override
    public PostDto addPost(PostDto postDto, List<MultipartFile> files) {
        try{
            System.out.println(postDto.getType()+" "+ PostDto.safeTypeConversion(postDto.getType()));

            Post post = Post.builder()
                    .title(postDto.getTitle())
                    .content(postDto.getContent())
                    .links(postDto.getLinks())
                    .date(postDto.getDate())
                    .tags(postDto.getTags())
                    .author(postDto.getAuthor())
                    .type(PostDto.safeTypeConversion(postDto.getType()))
                    .downVoteNumber(0)
                    .seenNumber(0)
                    .upVoteNumber(0)
                    .isActive(true)
                    .build();

            Post savedPost=postRepository.save(post);
            List<String> mediaList=new ArrayList<>();
            if(!files.isEmpty()){
                for (MultipartFile file : files) {
                    String imageUrl;
                    try {
                        imageUrl=cloudinaryService.uploadFileToFolder(file,"quizini/postImages/"+savedPost.getType()+"/"+savedPost.getId());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    mediaList.add(imageUrl);
                }
            }
            savedPost.setMediaUrls(mediaList);
            savedPost=postRepository.save(savedPost);
            return PostDto.mapToDto(savedPost);

        }
        catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Set<PostDto> getNewPosts(String email, Set<Long> cachedPostIds, int numberOfPosts) {
        UserEntity user=userRepository.findByEmail(email).orElse(null);
        if(user==null){
            throw new RuntimeException("User not found");
        }
        for(Long postId : cachedPostIds){
            Post post=postRepository.findById(postId).orElse(null);
            if(post==null){
                throw new RuntimeException("Post not found id: "+postId);
            }
        }

        try{
            Set<Long> wantedPostsByTopic=getWantedPostsByTopic(user,cachedPostIds);
            System.out.println("wanted posts "+wantedPostsByTopic);
            List<Post> posts= postRepository.findAllById(wantedPostsByTopic);
            System.out.println("Posts are : "+posts);

            List<Post> sortedByMostUpvoted = PostUtils.sortPostsByAllCriteria(posts);
            System.out.println("Sorted list: " + sortedByMostUpvoted);
            List<Post> firstXElements = posts.subList(0, Math.min(numberOfPosts, posts.size()));

            for(Post post:firstXElements){
                PostUserAction postUserActionOld=postUserRepository.findByUser_IdAndPost_Id(user.getId(),post.getId()).orElse(null);
                if(postUserActionOld==null) {
                    post.setSeenNumber(post.getSeenNumber() + 1);
                    PostUserAction postUserAction = PostUserAction.builder()
                            .seen(true)
                            .seenAt(Timestamp.valueOf(LocalDateTime.now()))
                            .upvoted(false)
                            .downvoted(false)
                            .user(user)
                            .post(post)
                            .build();
                    postUserRepository.save(postUserAction);
                    postRepository.save(post);
                }

            }
            return firstXElements.stream().map(PostDto::mapToDto).collect(Collectors.toSet());
        }
        catch(Exception e){
            throw new RuntimeException(e.getMessage());
        }

    }

    Set<Long> getWantedPostsByTopic(UserEntity user,Set<Long> cachedPostIds){
        Set<Long> wantedPostsByTopic=new HashSet<>();
        Set<Integer> wantedTopics=userTopicRepository.findUserTopicsByUserId(user.getId());
        System.out.println("********wantedTopics: "+wantedTopics);

        if(!wantedTopics.isEmpty()) {

            for (Integer topicId : wantedTopics) {
                Topic topic = topicRepository.findById(topicId).orElse(null);
                if (topic == null) {
                    throw new RuntimeException("Topic not found id: " + topicId);
                }
                String topicName = topic.getName();
                System.out.println(topicName);
                List<Long> postsByTag=postRepository.findPostIdsByTag(topicName);
                wantedPostsByTopic.addAll(postsByTag);

            }
        }
        else
        {
            List<Topic> randomTopics = topicRepository.findThreeRandomTopics();
            for (Topic topic : randomTopics) {
                String topicName = topic.getName();
                System.out.println(topicName);
                List<Long> postsByTag=postRepository.findPostIdsByTag(topicName);
                wantedPostsByTopic.addAll(postsByTag);
            }

        }
        wantedPostsByTopic.removeAll(cachedPostIds);
        return wantedPostsByTopic;

    }
}
