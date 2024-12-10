package code.service.admin;

import code.exception.NotFoundException;
import code.model.entity.User;
import code.model.more.Conversation;
import code.model.more.Message;
import code.model.request.ChatRequest;
import code.repository.ConversationRepository;
import code.repository.MessageRepository;
import code.repository.UserRepository;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service("AdminChatService")
public class ChatService {

  private ConversationRepository conversationRepository;
  private MessageRepository messageRepository;
  private UserRepository userRepository;

  public ChatService(ConversationRepository conversationRepository,
      MessageRepository messageRepository,
      UserRepository userRepository) {
    this.conversationRepository = conversationRepository;
    this.messageRepository = messageRepository;
    this.userRepository = userRepository;
  }

//  Lấy tất cả cuộc trò chuyện sắp xếp theo thứ tự cập nhật : mới đến cũ
  public Page<Conversation> getConversations(int page, int size){
//    Lấy phân trang và sắp xếp từ mới đến cũ
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
    return conversationRepository.findAll(pageable);
  }

//  Lấy cuộc trò chuyện theo id
  public Conversation getConversationById(long id){
    return conversationRepository.findById(id)
        .orElseThrow(()-> new NotFoundException("Không thấy conversation có id : " + id));
  }

//  Lấy nội dung tin nhắn : load cố định 20 tin nhắn trên Page
  public Page<Message> getMessagesByConversation(long conversationId,int page){
    Pageable pageable = PageRequest.of(page,20, Sort.by(Sort.Direction.DESC, "createdAt"));
    Conversation conversation = conversationRepository.findById(conversationId)
        .orElseThrow(()-> new NotFoundException("Không thấy Conversation có id : "+ conversationId));
    return messageRepository.findALlByConversation(pageable,conversation);
  }

//  Chat voi customer
//  Gui tin nhan toi customer co id la customerId
  public Message chatToCustomer(long customerId, ChatRequest request){
    User customer = userRepository.findByIdAndRole(customerId,"customer")
        .orElseThrow(
            () -> new NotFoundException("Không tìm thấy Customer có id : " + customerId));
//    Lấy cuộc trò chuyện ra theo customerId
    Conversation conversation = conversationRepository.findByCustomerId(customer.getId())
        .orElseThrow(() -> new NotFoundException(
            "Không thấy Conversation tương ứng với CustomerId : " + customerId));
//    Tạo đối tượng tin nhắn mới
    Message message = new Message();
    message.setSenderId(0);
    message.setSenderRole("admin");
    message.setReceiverId(customerId);
    message.setReceiverRole("customer");
    message.setContent(request.getContent());
    message.setConversation(conversation);
//    Cập nhật dữ liệu cho Conversation như tin nhắn cuối, thời gian cập nhật, ....
    LocalDateTime now = LocalDateTime.now();
    message.setCreatedAt(now);
    conversation.setUpdatedAt(now);
    conversation.setLastSenderId(0);
    conversation.setLastMessageContent(message.getContent());
//    Lưu tin nhắn và cập nhật thời gian Conversation cùng lúc
    messageRepository.save(message);
    conversationRepository.save(conversation);
    return message;
  }

//  seen tin nhắn của customer co id la customerId : tất cả admin đều có thể seen được
  public int seenMessageFromCustomer(long customerId){
    User customer = userRepository.findById(customerId)
        .orElseThrow(()-> new NotFoundException("Không thấy Message có id : " + customerId));
    int messages = messageRepository.seenMessageFromCustomer(customerId,"customer");
    return messages;
  }
}
