package code.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class WebHookRequest {
  private Long id;
  private String gateway; // Brand name của ngân hàng
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime transactionDate; // Thời gian xảy ra giao dịch phía ngân hàng
  private String accountNumber; // Số tài khoản ngân hàng
  private String code; // Mã code thanh toán
//  Dat hang
//  SEVQR_01_MaKH_MaDonHang - dathang
//  SEVQR_02_MaKH_MaDonHangDetail - loi
  private String content; // Nội dung chuyển khoản
  private String transferType; // Loại giao dịch (in hoặc out)
  private Long transferAmount; // Số tiền giao dịch
  private Long accumulated; // Số dư tài khoản (lũy kế)
  private String subAccount; // Tài khoản ngân hàng phụ (tài khoản định danh)
  private String referenceCode; // Mã tham chiếu của tin nhắn SMS
  private String description; // Toàn bộ nội dung tin nhắn SMS
}
