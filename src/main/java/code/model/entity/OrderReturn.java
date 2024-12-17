package code.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "order_returns")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
// trar hàng, admin tìm OrderReturn để thêm thông tin
// nhập ngày trả
// TÍnh phí thêm
// Nhấn OK : tiền thanh toán phí thêm là chưa trả
public class OrderReturn {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "return_date", nullable = false)
  private Date returnDate;

  @Column(name = "quantity", nullable = false)
  private long quantity;

  @Column(name = "quantity_loss", nullable = false)
  private long quantityLoss;

  @Column(name = "product_condition", nullable = false)
  private String condition;

  @Column(name = "overdue_date", nullable = false)
  private long overdueDate;

  //  Phụ phí khách hàng phải trả do quá hạn
  @Column(name = "overdue_fee", nullable = false)
  private long overdueFee;

  //  Phụ phí khách hàng phải trả do hỏng hóc hoặc mất mát
  @Column(name = "damage_or_loss_fee", nullable = false)
  private long damageOrLossFee;

  //  Mô tả thiệt hại khách hàng phải đền : muộn bao nhiêu ngày từ ngày nào đến ngày nào;
  //  Hỏng bao nhiêu cái, tình trạng như nào;
  //  Mất bao nhiêu cái;
  @Column(name = "feeDetail", nullable = false)
  private String feeDetail;

  @Column(name = "created_at", nullable = false)
  @CreationTimestamp
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "order_detail_id", referencedColumnName = "id")
  private OrderDetail orderDetail;
}
