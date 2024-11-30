package SGBD_Project.example.LearnCode.Services;

import SGBD_Project.example.LearnCode.Dto.ConversationDto;
import SGBD_Project.example.LearnCode.Models.MessagePair;

import java.util.List;

public interface ConversationService {
    List<ConversationDto> getConversations(String email, int conversationsQt);

    ConversationDto getConversationById(String email, Long conversationId);

    ConversationDto createConversation(String email,MessagePair messagePair);

    ConversationDto addQuestion(String email, String question, Long conversationId);
}
