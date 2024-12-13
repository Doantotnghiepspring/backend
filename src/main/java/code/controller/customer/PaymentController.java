package code.controller.customer;

import code.controller.socket.WebSocketController;
import code.model.more.Transaction;
import code.model.request.WebHookRequest;
import code.service.transaction.TransactionService;
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
    Transaction transaction = transactionService.addTransactionFromWebHook(request);

    // Tạo JSON trả về
    Map<String, Object> response = new HashMap<>();
    response.put("success", true);
    response.put("data", transaction); // Chèn dữ liệu kết quả từ service

//  Gui socket thong bao toi admin
    webSocketController.newOrderPaid(response);

//    Gui socket thong bao thanh toan thnh cong toi customer
    webSocketController.customerPaySuccess(transaction.getCustomerId(), response);

    // Trả về HTTP Status 201 với JSON
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

}
