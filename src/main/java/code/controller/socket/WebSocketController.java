package code.controller.socket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebSocketController {

  @Autowired
  private SimpMessagingTemplate messagingTemplate;

  //  Admin gửi tin nhắn tới customer
  public void adminSendToCustomer(long customerId, Object message) {
    String destination = "/send/customer/" + customerId;
    messagingTemplate.convertAndSend(destination, message);
  }

  // Admin seen tất cả tin nhắn của customer
  public void adminSeenMessage(long customerId, Object conversation) {
    String destination = "/seen/customer/" + customerId + "/seen";
    messagingTemplate.convertAndSend(destination, conversation);
  }

  //  Customer gửi tin nhắn tới admin
  public void customerSendToAdmin(Object message) {
    String destination = "/send/admin";
    messagingTemplate.convertAndSend(destination, message);
  }

  // Customer seen tất cả tin nhắn của admin
  public void customerSeenMessage(long customerId, Object message) {
    String destination = "/customers/" + customerId + "/seen";
    messagingTemplate.convertAndSend(destination, message);
  }

  //  Thông báo sau khi khách thanh toán thành công
  public void customerPaySuccess(long customerId, Object payOK) {
    String destination = "/pay/customers/" + customerId;
    messagingTemplate.convertAndSend(destination, payOK);
  }

  //  Thong bao cho admin sau khi co don hang da thanh toan
  public void newOrderPaid(Object message) {
    String destination = "/notify/newOrderPaid";
    messagingTemplate.convertAndSend(destination, message);
  }

}
