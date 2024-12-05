package code.service.transaction;

import code.exception.NotFoundException;
import code.model.entity.Order;
import code.model.entity.OrderDetail;
import code.model.more.Transaction;
import code.model.request.WebHookRequest;
import code.repository.OrderDetailRepository;
import code.repository.OrderRepository;
import code.repository.TransactionRepository;
import code.service.customer.OrderDetailService;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

  private TransactionRepository transactionRepository;
  private OrderDetailRepository orderDetailRepository;
  private OrderRepository orderRepository;

  public TransactionService(TransactionRepository transactionRepository,
      OrderDetailRepository orderDetailRepository,
      OrderRepository orderRepository) {
    this.transactionRepository = transactionRepository;
    this.orderDetailRepository = orderDetailRepository;
    this.orderRepository = orderRepository;
  }

  //  lấy giá trị webhook lưu vào db
  public Transaction addTransactionFromWebHook(WebHookRequest request) {
    Transaction transaction = new Transaction();
    BeanUtils.copyProperties(request, transaction);
    transaction.setIdSepay(request.getId());
    transaction.setId(null);
    transactionRepository.save(transaction);
//    Thay doi trang thai đơn hàng : 1 -> 2
//    Lay thong tin (*******SEVQR_makhachhang_madonhang)

    String content = request.getContent();
    String[] parts = content.split("_", 3);
    long makhachhang = Long.parseLong(parts[1]);
    long madonhang = Long.parseLong(parts[2]);
    Order order = orderRepository.findById(madonhang)
        .orElseThrow(() -> new NotFoundException("Không timg thấy Order có id : " + madonhang));
    List<OrderDetail> orderDetails = orderDetailRepository.findByOrder(order);
    for(OrderDetail orderDetail : orderDetails){
      orderDetail.setStatus(2);
      orderDetailRepository.save(orderDetail);
    }
    return transaction;
  }
//  mở QR
//  ng dùng quét -> lưu đc giao dịch vào db -> socket gửi về fe để xác nhận thành công ->đóng QR
//  -> chuyển đến orders

//  Mã nội dung ck : yêu cầu chứa "SEVQR" : SEVQR + Mã KH + Mã Order
//diện thoại + máy chiếu
//  Tạo 1 Order -> orderDetail
}
