package code.model.request;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CreateOrderReturnRequest {
  private long quantityLoss;
  private Date returnDate;
  private long quantity;
  private long overdueDate;
  private long overdueFee;
  private long damageOrLossFee;
  private String feeDetail;
//  Nếu tình trạng như cũ thì sẽ cộng thêm số lượng có sẵn trong kho
//  Nếu tình trạng khác thì tạo productDetail mới dựa theo tình trạng hiện tại
  private String condition;

  public long totalFee(){
    return this.overdueFee + this.damageOrLossFee;
  }
}
