package SGBD_Project.example.LearnCode.Services.Impl;

import SGBD_Project.example.LearnCode.Dto.ConversationDto;
import SGBD_Project.example.LearnCode.Models.Conversation;
import SGBD_Project.example.LearnCode.Models.MessagePair;
import SGBD_Project.example.LearnCode.Models.UserEntity;
import SGBD_Project.example.LearnCode.Repositories.ConversationRepository;
import SGBD_Project.example.LearnCode.Repositories.MessagePairRepository;
import SGBD_Project.example.LearnCode.Repositories.UserRepository;
import SGBD_Project.example.LearnCode.Services.ConversationService;
import SGBD_Project.example.LearnCode.Services.FlaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final MessagePairRepository messagePairRepository;
    private final FlaskService flaskService;

    @Autowired
    public ConversationServiceImpl(ConversationRepository conversationRepository, UserRepository userRepository, MessagePairRepository messagePairRepository, FlaskService flaskService) {
            this.conversationRepository = conversationRepository;
        this.userRepository = userRepository;
        this.messagePairRepository = messagePairRepository;
        this.flaskService = flaskService;
    }

    @Override
    public List<ConversationDto> getConversations(String email, int conversationsQt) {
        UserEntity user=userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User Not Found"));
        List<Conversation> conversations=conversationRepository.findTopConversationsByUserId(user.getId(),PageRequest.of(0,conversationsQt));
        for(Conversation conversation:conversations){
            List<MessagePair> messagePairs=messagePairRepository.findTopMessagesByConversationId(conversation.getId(),PageRequest.of(0,conversationsQt));
            conversation.setMessages(messagePairs);
        }
        return conversations.stream().map(ConversationDto::conversationToDto).collect(Collectors.toList());
    }

    @Override
    public ConversationDto getConversationById(String email, Long conversationId) {
        UserEntity user=userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User Not Found"));
        Conversation conversation=conversationRepository.findByIdAndUserId(conversationId,user.getId()).orElseThrow(()->new RuntimeException("Conversation Not Found"));
        ConversationDto conversationDto= ConversationDto.conversationToDto(conversation);
        List<MessagePair> messagePairs=messagePairRepository.findTopMessagesByConversationId(conversationId,PageRequest.of(0,5));
        conversationDto.setMessages(messagePairs);
        return ConversationDto.conversationToDto(conversation);

    }

    @Override
    public ConversationDto createConversation(String email,MessagePair messagePair) {
        UserEntity user=userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User Not Found"));
        Conversation conversation=new Conversation();
        conversation.setUser(user);
        conversation.setValid(true);
        Conversation savedConv=conversationRepository.save(conversation);
        messagePair.setValid(true);
        messagePair.setConversation(savedConv);

        // i should get the answer by the flask model
        Map<String,Object> flaskResponse=flaskService.sendChatBot(messagePair.getQuestion());
        messagePair.setAnswer(flaskResponse.get("response").toString());
        MessagePair savedMessagePair=messagePairRepository.save(messagePair);
       // String title = Arrays.stream(messagePair.getQuestion().split(" ")).limit(5).collect(Collectors.joining(" "));
        String title=flaskResponse.get("topic").toString();
        System.out.println("the title will be : "+title);
        conversation.setTitle(title);
        conversation.getMessages().add(savedMessagePair);
        conversationRepository.save(conversation);
        return ConversationDto.conversationToDto(conversation);

    }

    @Override
    public ConversationDto addQuestion(String email, String question, Long conversationId) {
        UserEntity user=userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User Not Found"));
        Conversation conversation=conversationRepository.findByIdAndUserId(conversationId,user.getId()).orElseThrow(()->new RuntimeException("Conversation not found for this user"));
        if(!conversation.isValid()){
            throw new RuntimeException("Conversation is not valid");
        }
        Map<String,Object> flaskResponse=flaskService.sendChatBot(question);
        MessagePair messagePair=new MessagePair();
        messagePair.setQuestion(question);
        messagePair.setValid(true);
        messagePair.setConversation(conversation);
        messagePair.setAnswer(flaskResponse.get("response").toString());
        messagePairRepository.save(messagePair);
        conversation.getMessages().add(messagePair);
        if(conversation.getTitle().equals("UnKnown")){
            if(!flaskResponse.get("topic").toString().equals("UnKnown")){
                conversation.setTitle(flaskResponse.get("topic").toString());
            }
        }
        Conversation savedConversation=conversationRepository.save(conversation);
        savedConversation.setMessages(List.of(messagePair));
        return ConversationDto.conversationToDto(conversation);

    }
}
