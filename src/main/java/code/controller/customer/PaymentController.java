package code.controller.customer;

import code.controller.more.WebSocketController;
import code.model.more.Notification;
import code.model.more.Transaction;
import code.model.request.WebHookRequest;
import code.service.more.TransactionService;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

  private TransactionService transactionService;
  private WebSocketController webSocketController;

  public PaymentController(TransactionService transactionService,
      WebSocketController webSocketController) {
    this.transactionService = transactionService;
    this.webSocketController = webSocketController;
  }

  //  API webhook real
  @PostMapping("/webhook")
  public ResponseEntity<?> addTransaction(@RequestBody WebHookRequest request) {
    // Gọi service để xử lý request
    Map<String,Object> map  = transactionService.addTransactionFromWebHook(request);
    Notification notification= (Notification) map.get("notification");
    Transaction transaction = (Transaction) map.get("transaction") ;

    // Tạo JSON trả về
    Map<String, Object> response = new HashMap<>();
    response.put("success", true);
    response.put("notification", notification); // Chèn dữ liệu kết quả từ service

//  Gui socket thong bao toi admin neu khach hang thanh toan don dat hang
    if(transaction.getTypePayment() == 1){
      webSocketController.customerPaySuccessForadmin(notification);
      webSocketController.customerPaySuccess(transaction.getCustomerId(), "Thanh toán thành công");
    }
    if(transaction.getTypePayment() == 2){
      webSocketController.customerPayFeeSuccessForadmin(notification);
      webSocketController.customerPayFeeSuccess(transaction.getCustomerId(),"Thanh toán phí thành công");
    }
    // Trả về HTTP Status 201 với JSON
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
}
