package SGBD_Project.example.LearnCode.Services;

import SGBD_Project.example.LearnCode.Dto.ConversationDto;
import SGBD_Project.example.LearnCode.Models.MessagePair;

import java.util.List;

public interface ConversationService {
    List<ConversationDto> getConversations(String email, int conversationsQt,List<Long> existedConversationsId);

    ConversationDto getConversationById(String email, Long conversationId,int messagePairQt,List<Long> existedMessagePairsId);

    ConversationDto createConversation(String email,MessagePair messagePair);

    ConversationDto addQuestion(String email, String question, Long conversationId);

    MessagePair likeAnswer(String email, Long conversationId, Long messagePairId);

    MessagePair dislikeAnswer(String email, Long conversationId, Long messagePairId);
}
