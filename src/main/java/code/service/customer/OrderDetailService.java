package code.service.customer;

import code.exception.BadRequestException;
import code.exception.NotFoundException;
import code.model.entity.Order;
import code.model.entity.OrderDetail;
import code.model.entity.ProductDetail;
import code.model.entity.User;
import code.model.more.Notification;
import code.model.request.CreateOrderDetailRequest;
import code.model.request.ProductItem;
import code.repository.NotificationRepository;
import code.repository.OrderDetailRepository;
import code.repository.OrderRepository;
import code.repository.ProductDetailRepository;
import code.repository.UserRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service("CustomerOrderService")
public class OrderDetailService {

  private OrderDetailRepository orderDetailRepository;
  private OrderRepository orderRepository;
  private UserRepository userRepository;
  private ProductDetailRepository productDetailRepository;
  private NotificationRepository notificationRepository;

  public OrderDetailService(OrderDetailRepository orderDetailRepository,
      UserRepository userRepository,
      ProductDetailRepository productDetailRepository,
      OrderRepository orderRepository,
      NotificationRepository notificationRepository) {
    this.orderDetailRepository = orderDetailRepository;
    this.userRepository = userRepository;
    this.orderRepository = orderRepository;
    this.notificationRepository = notificationRepository;
    this.productDetailRepository = productDetailRepository;
  }

  //  Lấy tất cả các đơn hàng chi tiết (OrderDetail)
  public Page<OrderDetail> getOrderDetailsByUser(User user, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    return orderDetailRepository.findAllByUserId(user.getId(), pageable);
  }

  //  Lấy các đơn hàng theo trạng thái
  public Page<OrderDetail> getAllByUserAndProductDetailStatus(User user, int status, int page,
      int size) {
    Pageable pageable = PageRequest.of(page, size);
    return orderDetailRepository.findAllByUserIdAndProductDetailStatus(user.getId(), status, pageable);
  }

  // Xem đơn hàng cụ thể có id là orderDetailId
  public OrderDetail getByUserIdAndId(User user, long orderDetailId) {
    OrderDetail orderDetail = orderDetailRepository.findByOrderDetailIdAndUserId(orderDetailId,
            user.getId())
        .orElseThrow(() -> new NotFoundException(
            "Không tìm thấy đơn hàng tương ứng "));
    return orderDetail;
  }

  //  Tạo mới đơn hàng : Tạo đối tượng order -> xửa lí List<productDetailId> và trạng thái 1 là chưa thanh toán
  public List<OrderDetail> createOrderDetail(User user, CreateOrderDetailRequest request) {
  //    1 - Tạo Order mới
    Order order = new Order();
    order.setUser(user);
    order.setPayment(request.getPayment());
    order.setShipment(request.getShipment());
    order.setCurrentAddress(request.getCurrentAddress());
    order.setCurrentPhone(request.getCurrentPhone());
    orderRepository.save(order);
  //      Xử lý danh sách các ProductDetail x Quantity
    List<OrderDetail> orderDetails = new ArrayList<>();
    for (ProductItem productItem : request.getProductItems()) {
      long productDetailId = productItem.getProductDetailId();
      ProductDetail productDetail = productDetailRepository.findById(productDetailId)
          .orElseThrow(
              () -> new NotFoundException("Không thấy ProductDetail có id : " + productDetailId));
      OrderDetail orderDetail = new OrderDetail();

      orderDetail.setRentalDay(productItem.getRentalDay());
      orderDetail.setCurrentPrice(productDetail.getPrice());

  //      Check số lượng đặt hàng với số trong kho
      if (productDetail.getInventory() < productItem.getQuantity()) {
        throw new BadRequestException("Số lượng quá giới hạn");
      }
      orderDetail.setQuantity(productItem.getQuantity());
      orderDetail.setCurrentCondition(productDetail.getCondition());
      orderDetail.setNote(productItem.getNote());
      orderDetail.setStatus(1);
      orderDetail.setOrder(order);
      orderDetail.setProductDetail(productDetail);
      orderDetails.add(orderDetail);
      orderDetailRepository.save(orderDetail);
    }
    return orderDetails;
  }

  public OrderDetail cancelOrderDetail(User user, long orderDetailId) {
    OrderDetail orderDetail = orderDetailRepository.findByOrderDetailIdAndUserId(orderDetailId,
            user.getId())
        .orElseThrow(() -> new NotFoundException(
            "Không tìm thấy đơn hàng tương ứng "));
    if (orderDetail.getStatus() == 1 ) {
      orderDetail.setStatus(0);
    } else {
      throw new BadRequestException("Không thể hủy đơn hàng ở trạng thái này.");
    }
    return orderDetail;
  }

//  đơn hàng ở stt4 : đã nhận được hàng và khách hàng có nhu cầu trả hàng -> chuyển status từ 4->5
public Map<String,Object> wanToReturnOrderDetail(User user, long orderDetailId) {
  OrderDetail orderDetail = orderDetailRepository.findByOrderDetailIdAndUserId(orderDetailId,
          user.getId())
      .orElseThrow(() -> new NotFoundException(
          "Không tìm thấy đơn hàng tương ứng "));
  Notification notification = new Notification();
  if (orderDetail.getStatus() == 4 ) {
    orderDetail.setStatus(5);
    notification.setOrderId(orderDetailId);
    notification.setRoleReceive("admin");
    notification.setContent("Đơn hàng "+orderDetailId+" đang được người dùng muốn trả");
    notification.setUserReceiveId(0);
    notification.setStatus(false);
    notificationRepository.save(notification);
  } else {
    throw new BadRequestException("Lỗi khi thay đổi trạng thái.");
  }
  Map<String,Object> map = new HashMap<>();
  map.put("notification",notification);
  map.put("orderDetail",orderDetail);
  return map;
}

}
