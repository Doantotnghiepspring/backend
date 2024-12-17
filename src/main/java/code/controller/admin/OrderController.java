package code.controller.admin;

import code.controller.more.WebSocketController;
import code.model.entity.OrderDetail;
import code.model.entity.OrderReturn;
import code.model.entity.User;
import code.model.more.Notification;
import code.model.request.CreateOrderReturnRequest;
import code.service.admin.OrderService;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("AdminOrderController")
@RequestMapping("/api/admin")
public class OrderController {

  private OrderService orderService;
  private WebSocketController webSocketController;

  public OrderController(OrderService orderService, WebSocketController webSocketController) {
    this.orderService = orderService;
    this.webSocketController = webSocketController;
  }

  //  Lấy tất cả đơn hàng
  @GetMapping("/orders")
  public ResponseEntity<?> getOrders(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    return ResponseEntity.ok(orderService.getOrderDetails(page, size));
  }

  //  Lấy đơn hàng chi tiết theo id
  @GetMapping("/orders/{orderDetailId}")
  public ResponseEntity<?> getOrderById(@PathVariable long orderDetailId) {
    return ResponseEntity.ok(orderService.getOrderDetailById(orderDetailId));
  }

  //  Thay đổi trạng thái đơn hàng
  @PutMapping("/orders/{orderDetailId}")
  public ResponseEntity<?> updateOrderById(@PathVariable long orderDetailId,
      @RequestParam int status) {
    Map<String, Object> response = (Map<String, Object>) orderService.updateStatusOrderDetailById(
        orderDetailId, status);

    OrderDetail orderDetail = (OrderDetail) response.get("orderDetail");
    User customer = (User) response.get("user");

//    Don hang tu trang thai 2 len 3 ; dang VC
    if (status == 3) {
      webSocketController.deliverOrderDetail(customer.getId(),orderDetail);
    }
    if(status == 4){
      webSocketController.reserveOrderDetail(customer.getId(),orderDetailId,orderDetail);
    }
    return ResponseEntity.ok(response);
  }

// Tạo OrderReturn
  @PostMapping("/orders/{orderDetailId}/return")
  public ResponseEntity<?> createOrderReturn(@PathVariable long orderDetailId,
      @RequestBody CreateOrderReturnRequest request) {
    OrderReturn orderReturn = orderService.createOrderReturn(orderDetailId, request);
    webSocketController.newOrderReturn(orderReturn.getOrderDetail().getOrder().getUser().getId(),
        orderDetailId,orderReturn);
    return ResponseEntity.ok(orderReturn);
  }


}
