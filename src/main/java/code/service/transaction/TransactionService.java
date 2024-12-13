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
//    Lay thong tin tu noi dung chuyen khoan
//    Thanh toan don hang(*******SEVQR_01_makhachhang_madonhang)
//    Nop phu phi qua han, mat, hong san pham(*******SEVQR_02_makhachhang_madonhang)

    String content = request.getContent();
    String[] parts = content.split("_", 4);
    long typePayment = Long.parseLong(parts[1]);
//    Neu ma la ******SEVQR_01_****** thi la chuyen khoan thanh toan don hang
    if(typePayment == 1){
      long makhachhang = Long.parseLong(parts[2]);
      long madonhang = Long.parseLong(parts[3]);
      Order order = orderRepository.findById(madonhang)
          .orElseThrow(() -> new NotFoundException("Không tìm thấy Order có id : " + madonhang));
      List<OrderDetail> orderDetails = orderDetailRepository.findByOrder(order);
      for(OrderDetail orderDetail : orderDetails){
        orderDetail.setStatus(2);
        orderDetailRepository.save(orderDetail);
      }
      return transaction;
    }
//    Neu ma la ******SEVQR_02_***** thi la thanh toan phu phi
    if(typePayment == 2){
      long makhachhang = Long.parseLong(parts[2]);
      long madonhang = Long.parseLong(parts[3]);
      OrderDetail orderDetail = orderDetailRepository.findById(madonhang)
          .orElseThrow(() -> new NotFoundException("Không tìm thấy OrderDetail có id : " + madonhang));
      orderDetail.setStatus(8);
      orderDetailRepository.save(orderDetail);
      return transaction;
    }
    return null;
  }
//  mở QR
//  ng dùng quét -> lưu đc giao dịch vào db -> socket gửi về fe để xác nhận thành công ->đóng QR
//  -> chuyển đến orders

//  Mã nội dung ck : yêu cầu chứa "SEVQR" : SEVQR + Mã KH + Mã Order
//diện thoại + máy chiếu
//  Tạo 1 Order -> orderDetail
}
