package SGBD_Project.example.LearnCode.Dto;

import SGBD_Project.example.LearnCode.Models.Conversation;
import SGBD_Project.example.LearnCode.Models.MessagePair;
import SGBD_Project.example.LearnCode.Models.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConversationDto {

    private Long id;
    private String title;
    private boolean isValid = true;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String userId;
    private List<MessagePair> messages = new ArrayList<>();

    public static ConversationDto conversationToDto(Conversation conversation) {
        return ConversationDto.builder()
                .id(conversation.getId())
                .title(conversation.getTitle())
                .isValid(conversation.isValid())
                .createdAt(conversation.getCreatedAt())
                .updatedAt(conversation.getUpdatedAt())
                .userId(conversation.getUser().getId().toString()) // Assuming `userId` is a String in the DTO
                .messages(conversation.getMessages())
                .build();
    }


    public static Conversation dtoToConversation(ConversationDto conversationDto, UserEntity user) {
        Conversation conversation = new Conversation();
        conversation.setId(conversationDto.getId());
        conversation.setTitle(conversationDto.getTitle());
        conversation.setValid(conversationDto.isValid());
        conversation.setCreatedAt(conversation.getCreatedAt());
        conversation.setUpdatedAt(conversation.getUpdatedAt());
        conversation.setUser(user);
        conversation.setMessages(conversationDto.getMessages());
        return conversation;
    }

}
