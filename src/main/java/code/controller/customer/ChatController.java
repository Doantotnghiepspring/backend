package code.controller.customer;

import code.controller.more.WebSocketController;
import code.model.entity.User;
import code.model.more.Message;
import code.model.request.ChatRequest;
import code.security.CustomUserDetails;
import code.service.customer.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("CustomerChatController")
@RequestMapping("/api/customer")
public class ChatController {
  private ChatService chatService;
  private WebSocketController webSocketController;
  public ChatController(ChatService chatService,WebSocketController webSocketController){
    this.chatService = chatService;
    this.webSocketController = webSocketController;
  }

  @GetMapping("/chat")
  public ResponseEntity<?> getMessages(@AuthenticationPrincipal CustomUserDetails userDetail,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size){
    return ResponseEntity.ok(chatService.getMessages(userDetail.getUser(),page,size ));
  }

//  Nhan tin voi admin
  @PostMapping("/chat")
  public ResponseEntity<?> chatToAdmin(@AuthenticationPrincipal CustomUserDetails userDetail,@RequestBody
  ChatRequest request){
    Message response = chatService.chatToAdmin(request,userDetail.getUser());
    webSocketController.customerSendToAdmin(response);
    return ResponseEntity.ok(response);
  }

//  Seen tat ca cac tin nhan admin gui den minh`
  @PutMapping("/chat")
  public ResponseEntity<?> seenMessageFormAdmin(@AuthenticationPrincipal CustomUserDetails userDetail){
    User customer = userDetail.getUser();
    int response = chatService.seenMessageFromAdmin(customer);
    webSocketController.customerSeenMessage(customer.getId(),response);
    return ResponseEntity.ok(response);
  }
}
