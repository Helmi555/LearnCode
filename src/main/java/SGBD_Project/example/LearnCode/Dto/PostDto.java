package SGBD_Project.example.LearnCode.Dto;

import SGBD_Project.example.LearnCode.Models.Enums.Type;
import SGBD_Project.example.LearnCode.Models.Post;
import SGBD_Project.example.LearnCode.Models.PostUserAction;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDto {

    private long id;

    private String title;

    private String content;

    private String author;

    private LocalDateTime date;

    private List<String> tags;

    private List<String> links;

    private String type;

    private List<String> mediaUrls;

    private Timestamp updatedAt;

    private Timestamp createdAt;

    private Integer upVoteNumber = 0;

    private Integer downVoteNumber = 0;

    private Integer seenNumber = 0;

    private Boolean isActive = true;

    private Boolean upVoted = false;

    private Boolean downVoted = false;


    public static PostDto mapToDtoWithVotes(Post post,Boolean upVoted,Boolean downVoted) {
        return PostDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .author(post.getAuthor())
                .date(post.getDate())
                .tags(post.getTags())
                .links(post.getLinks())
                .type(post.getType() != null ? post.getType().name() : null) // Convert Enum to String
                .mediaUrls(post.getMediaUrls())
                .updatedAt(post.getUpdatedAt())
                .createdAt(post.getCreatedAt())
                .upVoteNumber(post.getUpVoteNumber())
                .downVoteNumber(post.getDownVoteNumber())
                .seenNumber(post.getSeenNumber())
                .isActive(post.getIsActive())
                .upVoted(upVoted)
                .downVoted(downVoted)
                .build();
    }

    public static PostDto mapToDto(Post post) {
        return PostDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .author(post.getAuthor())
                .date(post.getDate())
                .tags(post.getTags())
                .links(post.getLinks())
                .type(post.getType() != null ? post.getType().name() : null) // Convert Enum to String
                .mediaUrls(post.getMediaUrls())
                .updatedAt(post.getUpdatedAt())
                .createdAt(post.getCreatedAt())
                .upVoteNumber(post.getUpVoteNumber())
                .downVoteNumber(post.getDownVoteNumber())
                .seenNumber(post.getSeenNumber())
                .isActive(post.getIsActive())
                .build();
    }

    // Convert PostDto to Post
    public static Post mapToPost(PostDto postDto) {
        Post post = new Post();
        post.setId(postDto.getId());
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setAuthor(postDto.getAuthor());
        post.setDate(postDto.getDate());
        post.setTags(postDto.getTags());
        post.setLinks(postDto.getLinks());
        post.setType(postDto.getType() != null ? Type.valueOf(postDto.getType()) : null); // Convert String to Enum
        post.setMediaUrls(postDto.getMediaUrls());
        post.setUpdatedAt(postDto.getUpdatedAt());
        post.setCreatedAt(postDto.getCreatedAt());
        post.setUpVoteNumber(postDto.getUpVoteNumber());
        post.setDownVoteNumber(postDto.getDownVoteNumber());
        post.setSeenNumber(postDto.getSeenNumber());
        post.setIsActive(postDto.getIsActive());
        return post;
    }

    public static Type safeTypeConversion(String type) {
        try {
            return Type.valueOf(type);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid type provided: " + type + ". Allowed values are: "
                    + Arrays.toString(Type.values()));
        }
    }

}
