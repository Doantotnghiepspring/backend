package code.controller.admin;

import code.model.request.CreateOrderReturnRequest;
import code.service.admin.OrderService;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("AdminOrderController")
@RequestMapping("/api/admin")
public class OrderController {
  private OrderService orderService;
  public OrderController(OrderService orderService){
    this.orderService = orderService;
  }
//  Lấy tất cả đơn hàng
  @GetMapping("/orders")
  public ResponseEntity<?> getOrders(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size){
    return ResponseEntity.ok(orderService.getOrderDetails(page,size));
  }
//  Lấy đơn hàng chi tiết theo id
  @GetMapping("/orders/{orderDetailId}")
  public ResponseEntity<?> getOrderById(@PathVariable long orderDetailId){
    return ResponseEntity.ok(orderService.getOrderDetailById(orderDetailId));
  }
//  Thay đổi trạng thái đơn hàng
  @PutMapping("/orders/{orderDetailId}")
  public ResponseEntity<?> updateOrderById(@PathVariable long orderDetailId,@RequestParam int status){
    return ResponseEntity.ok(orderService.updateStatusOrderDetailById(orderDetailId,status));
  }
//  Láy tất cả đơn hàng theo tên người dùng : tìm theo tên người dùng hoặc id người dùng
// Tạo OrderReturn
  @PostMapping("/orders/{orderDetailId}/return")
  public ResponseEntity<?> createOrderReturn(@PathVariable long orderDetailId,
      @RequestBody  CreateOrderReturnRequest request){
    return ResponseEntity.ok(orderService.createOrderReturn(orderDetailId,request));
  }
}
