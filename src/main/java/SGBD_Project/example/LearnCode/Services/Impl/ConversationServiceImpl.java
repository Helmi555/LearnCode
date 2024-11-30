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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    public List<ConversationDto> getConversations(String email, int conversationsQt,List<Long>existedConversationsId) {
        UserEntity user=userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User Not Found"));
        List<Conversation> conversations=conversationRepository.findRecentConversationsByUserId(user.getId(),conversationsQt+ existedConversationsId.size());
        List<Conversation> conversationsResult=new ArrayList<>();
        for(Conversation conversation:conversations){
            if(conversationsResult.size()<conversationsQt) {
                if (!existedConversationsId.contains(conversation.getId())) {
                    System.out.println("hhelo conv  " + conversation.getId() + existedConversationsId.contains(conversation.getId()));
                    List<MessagePair> latestMessages = messagePairRepository.findTopMessagesByConversationId(
                            conversation.getId(),
                            5
                    );
                    conversation.setMessages(latestMessages);
                    conversationsResult.add(conversation);
                }
            }
        }
        return conversationsResult.stream().map(ConversationDto::conversationToDto).collect(Collectors.toList());
    }

    @Override
    public ConversationDto getConversationById(String email, Long conversationId,int messagePairQt,List<Long> existedMessagePairsId) {
        UserEntity user=userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User Not Found"));
        Conversation conversation=conversationRepository.findByIdAndUserId(conversationId,user.getId()).orElseThrow(()->new RuntimeException("Conversation Not Found"));
        ConversationDto conversationDto= ConversationDto.conversationToDto(conversation);
        List<MessagePair> latestMessages = messagePairRepository.findTopMessagesByConversationId(
                conversationId,
                messagePairQt+existedMessagePairsId.size()
        );
        List<MessagePair> messagePairsResult=new ArrayList<>();
        for(MessagePair messagePair:latestMessages){
            if(messagePairsResult.size()<messagePairQt) {
                if (!existedMessagePairsId.contains(messagePair.getId())) {
                    messagePairsResult.add(messagePair);
                }
            }
        }
        conversationDto.setMessages(messagePairsResult);
        return conversationDto;

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

    @Override
    public MessagePair likeAnswer(String email, Long conversationId, Long messagePairId) {
        UserEntity user=userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User Not Found"));
        Conversation conversation=conversationRepository.findByIdAndUserId(conversationId,user.getId()).orElseThrow(()->new RuntimeException("Conversation not found for this user"));
        if(!conversation.isValid()){
            throw new RuntimeException("Conversation is not valid");
        }
        if(!Objects.equals(conversation.getUser().getId(), user.getId())){
            throw new RuntimeException("This user doesnt own this conversation");
        }
        MessagePair messagePair=messagePairRepository.findByIdAndConversation_Id(messagePairId,conversationId);
        if(messagePair==null){
            throw new RuntimeException("This messagePair doesnt exist");
        }

        if (messagePair.getIsLiked()==1){
            messagePair.setIsLiked(0);
        }
        else {
            messagePair.setIsLiked(1);
        }
        messagePairRepository.save(messagePair);
        return messagePair;
    }

    @Override
    public MessagePair dislikeAnswer(String email, Long conversationId, Long messagePairId) {
        UserEntity user=userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User Not Found"));
        Conversation conversation=conversationRepository.findByIdAndUserId(conversationId,user.getId()).orElseThrow(()->new RuntimeException("Conversation not found for this user"));
        if(!conversation.isValid()){
            throw new RuntimeException("Conversation is not valid");
        }
        if(!Objects.equals(conversation.getUser().getId(), user.getId())){
            throw new RuntimeException("This user doesnt own this conversation");
        }
        MessagePair messagePair=messagePairRepository.findByIdAndConversation_Id(messagePairId,conversationId);
        if(messagePair==null){
            throw new RuntimeException("This messagePair doesnt exist");
        }

        if (messagePair.getIsLiked()==-1){
            messagePair.setIsLiked(0);
        }
        else {
            messagePair.setIsLiked(-1);
        }
        messagePairRepository.save(messagePair);
        return messagePair;
    }

}
