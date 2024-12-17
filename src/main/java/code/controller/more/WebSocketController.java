package code.controller.more;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebSocketController {

  @Autowired
  private SimpMessagingTemplate messagingTemplate;

  //  Admin gửi tin nhắn tới customer
  public void adminSendToCustomer(long customerId, Object message) {
    String destination = "/admin/send/customer/" + customerId;
    messagingTemplate.convertAndSend(destination, message);
  }

  // Admin seen tất cả tin nhắn của customer
  public void adminSeenMessage(long customerId, Object conversation) {
    String destination = "/admin/seen/customer/" + customerId;
    messagingTemplate.convertAndSend(destination, conversation);
  }

  //  Customer gửi tin nhắn tới admin
  public void customerSendToAdmin(Object message) {
    String destination = "/customer/send/admin";
    messagingTemplate.convertAndSend(destination, message);
  }

  // Customer seen tất cả tin nhắn của admin
  public void customerSeenMessage(long customerId, Object message) {
    String destination = "/customer/" + customerId + "/seen";
    messagingTemplate.convertAndSend(destination, message);
  }

  //================Xử lý quá trình đặt hàng==================
//  1. Khách hàng tạo đơn hàng
//  2. Khách hàng thanh toán chuyển khoản thành công
//  2.1 Trả về thông báo (đơn giản chỉ là thông tin đã pay thành công) thành công cho phía khách hàng biết
  public void customerPaySuccess(long customerId, String status) {
    String destination = "/customer/" + customerId + "/order/paySuccess";
    messagingTemplate.convertAndSend(destination, status);
  }

  //  2.2 Trả về thông báo(đối tượng Notification) cho phía admin biết để chuẩn bị đơn hàng
  public void customerPaySuccessForadmin( Object obj) {
    String destination = "/admin/order/paySuccess";
    messagingTemplate.convertAndSend(destination, obj);
  }

  //  3. Đơn hàng đang trên dường giao : thông báo dạng Notification cho khách
  public void deliverOrderDetail(long customerId, Object obj) {
    String destination = "/customer/" + customerId + "/order/deliver";
    messagingTemplate.convertAndSend(destination, obj);
  }

  //  4. Đơn hàng đã được nhận bởi khách hàng : thông báo dạng Notif cho khách
  public void reserveOrderDetail(long customerId, Object obj) {
    String destination = "/customer/" + customerId + "/order/receive";
    messagingTemplate.convertAndSend(destination, obj);
  }

  // 5. Khách muốn trả hàng : thông báo Notif cho admin
  public void customerWantToReturn(Object obj) {
    String destination = "/admin/order/wantToReturn";
    messagingTemplate.convertAndSend(destination, obj);
  }

  // 6. Đã nhận lại sản phẩm thành công và kiểm tra
  // 7. Thông báo OrderReturn cho khách để nếu có mất phí khách biết
  public void newOrderReturn(long customerId, Object obj) {
    String destination = "/customer/" + customerId + "/order/return";
    messagingTemplate.convertAndSend(destination, obj);
  }

  // 8. Khách hàng đã thanh toán phụ phí thành công
//  8.1 Thông báo cho admin
  public void customerPayFeeSuccessForadmin(Object obj) {
    String destination = "/admin/fee/paySuccess";
    messagingTemplate.convertAndSend(destination, obj);
  }

  //  8.2 Thông báo đã thanh toán thành công cho customer
  public void customerPayFeeSuccess(long customerId, String obj) {
    String destination = "/customer/" + customerId + "/order/payFeeSuccess";
    messagingTemplate.convertAndSend(destination, obj);
  }

  public void test(String obj) {
    String destination = "/test";
    messagingTemplate.convertAndSend(destination, obj);
  }
}
