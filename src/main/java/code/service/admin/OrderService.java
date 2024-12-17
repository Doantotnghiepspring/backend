package code.service.admin;

import code.controller.more.WebSocketController;
import code.exception.*;
import code.model.entity.*;
import code.model.more.Notification;
import code.model.request.CreateOrderReturnRequest;
import code.repository.*;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service("AdminOrderService")
public class OrderService {

  private OrderDetailRepository orderDetailRepository;
  private OrderRepository orderRepository;
  private UserRepository userRepository;
  private ProductDetailRepository productDetailRepository;
  private OrderReturnRepository orderReturnRepository;
  private NotificationRepository notificationRepository;

  public OrderService(OrderDetailRepository orderDetailRepository,
      UserRepository userRepository,
      ProductDetailRepository productDetailRepository,
      OrderRepository orderRepository,
      OrderReturnRepository orderReturnRepository,
      NotificationRepository notificationRepository) {
    this.orderDetailRepository = orderDetailRepository;
    this.userRepository = userRepository;
    this.orderRepository = orderRepository;
    this.productDetailRepository = productDetailRepository;
    this.orderReturnRepository = orderReturnRepository;
    this.notificationRepository = notificationRepository;
  }

  //  Lấy tất cả đơn hàng
  public Page<OrderDetail> getOrderDetails(int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    return orderDetailRepository.findAll(pageable);
  }

  //  Lấy đơn hàng theo id
  public Object getOrderDetailById(long orderDetailId) {
    OrderDetail orderDetail = orderDetailRepository.findById(orderDetailId)
        .orElseThrow(
            () -> new NotFoundException("Không thấy OrderDetail có id : " + orderDetailId));
    Order order = orderDetail.getOrder();
    User user = order.getUser();
    Map<String, Object> response = new HashMap<>();
    response.put("orderDetail", orderDetail);
    response.put("user", user);
    return response;
  }

  //  Thay đổi trạng thái đơn hàng
  public Map<String, Object> updateStatusOrderDetailById(long orderDetailId, int status) {
    Notification notification = new Notification();
    OrderDetail orderDetail = orderDetailRepository.findById(orderDetailId)
        .orElseThrow(
            () -> new NotFoundException("Không thấy OrderDetail có id : " + orderDetailId));
    if (status < 0 || status > 8) {
      throw new BadRequestException("Trạng thái không hợp lệ");
    }
// Đã thanh toán -> đang vận chuyển
    if (status == 3) {
      if (orderDetail.getStatus() == 2) {
        orderDetail.setStatus(3);
        orderDetailRepository.save(orderDetail);
        ProductDetail productDetail = orderDetail.getProductDetail();
        productDetail.setInventory(productDetail.getInventory() - orderDetail.getQuantity());
        productDetailRepository.save(productDetail);
        notification.setOrderId(orderDetailId);
        notification.setRoleReceive("customer");
        notification.setContent("Đơn hàng "+orderDetailId+" đang được vận chuyển");
        notification.setUserReceiveId(orderDetail.getOrder().getUser().getId());
        notification.setStatus(false);
        notificationRepository.save(notification);
      } else {
        throw new BadRequestException("Không thể chuyển trạng thái");
      }
    }
//  Xác nhận đã giao đến nơi + Tạo trạng thái trả hàng : OrderReturn
    if (status == 4) {
      if (orderDetail.getStatus() == 3) {
        orderDetail.setStatus(4);
        orderDetailRepository.save(orderDetail);
        notification.setOrderId(orderDetailId);
        notification.setRoleReceive("customer");
        notification.setContent("Đơn hàng "+orderDetailId+" đã giao cho bạn");
        notification.setUserReceiveId(orderDetail.getOrder().getUser().getId());
        notification.setStatus(false);
        notificationRepository.save(notification);
      } else {
        throw new BadRequestException("Không thể chuyển trạng thái");
      }
    }

//  Đơn ở trạng thái : muốn trả hàng -> đã trả hàng thành công : 5->6
//  Trả xong : khong can thong bao cho khach hang
    if (status == 6) {
      if (orderDetail.getStatus() == 5) {
        orderDetail.setStatus(6);
        orderDetailRepository.save(orderDetail);
      } else {
        throw new BadRequestException("Không thể chuyển trạng thái");
      }
    }

    Order order = orderDetail.getOrder();
    User user = order.getUser();
    Map<String, Object> response = new HashMap<>();
    response.put("orderDetail", orderDetail);
    response.put("notification", notification);
    return response;
  }

//  Chuyển trạng thái OrderDetail từ 6->7
  public Map<String, Object> createOrderReturn(long orderDetailId, CreateOrderReturnRequest request){
    // Tạo OrderReturn
    OrderDetail orderDetail = orderDetailRepository.findById(orderDetailId)
        .orElseThrow(()-> new NotFoundException("Không tìm thấy OrderDetail có id:"+ orderDetailId));
      OrderReturn orderReturn = new OrderReturn();
    Notification notification = new Notification();
      orderReturn.setOrderDetail(orderDetail);
      BeanUtils.copyProperties(request,orderReturn);

//      Kiểm tra trạng thái
//    Nếu như cũ thì cộng lại quantity vào kho
//    Nếu mô tả khác : tạo ProductDetail mới và lưu vào kho với số lượng như đã có
    if(request.getCondition().equals("Như cũ")){
      ProductDetail productDetail = orderDetail.getProductDetail();
      productDetail.setInventory(productDetail.getInventory() + request.getQuantity());
      productDetailRepository.save(productDetail);
    }
    else{
      ProductDetail productDetail = new ProductDetail();
      BeanUtils.copyProperties(orderDetail.getProductDetail(),productDetail,"id","orderDetails","reviews");
      productDetail.setInventory(request.getQuantity());
      productDetail.setCondition(request.getCondition());
//      productDetail.setProduct(orderDetail.getProductDetail().getProduct());
      productDetailRepository.save(productDetail);
    }
      orderReturnRepository.save(orderReturn);
      orderDetail.setStatus(7);
//      Nếu không có phụ phí thì chuyển sang trnajg thái 8 luôn
    if(request.totalFee() == 0){
      orderDetail.setStatus(8);
    }

    else{
      notification.setOrderId(orderDetailId);
      notification.setRoleReceive("customer");
      notification.setContent("Đơn hàng "+orderDetailId+" yêu cầu bồi thường phí thiệt hại");
      notification.setUserReceiveId(orderDetail.getOrder().getUser().getId());
      notification.setStatus(false);
      notificationRepository.save(notification);
    }
      orderDetailRepository.save(orderDetail);
    Map<String, Object> response = new HashMap<>();
    response.put("orderReturn", orderReturn);
    response.put("notification", notification);
    return response;
  }
}
