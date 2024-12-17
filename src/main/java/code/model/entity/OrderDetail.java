package code.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name="order_details")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class OrderDetail {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "quantity",nullable = false)
  private long quantity;

  @Column(name = "current_price",nullable = false)
  private long currentPrice;

  @Column(name = "rental_day",nullable = false)
  private int rentalDay;

  @Column(name = "current_condition",nullable = false)
  private String currentCondition;

  @Column(name = "note",nullable = false)
  private String note;
  
  @Column(name = "status",nullable = false)
  private int status;
//  1 : Chưa thanh toán - Có thể hủy (chỉ khách hàng chuyển 1->0)
//  2 : Đã thanh toán đơn hàng xong chờ giao(chỉ admin chuyển 2->3)
//  3 : Đang giao(chỉ admin chuyển 3->4)
//  4 : Đã giao đến nơi -> Tạo 1 OrderReturn (chỉ khách chuyển 4->5)
//  5 : Khách hàng muốn trả đơn hàng(chỉ admin chuyển 5->6)
//  6 : Khách hàng đã trả xong và tạo OrderReturn để tính phí nếu hỏng, quá hạn, ...
//  7 : Có phát sinh thêm phí đang chờ KH thanh toán phí này
//  8 : Hoàn tất
//  0 : Đã hủy

//  cus : 1->0(hủy đơn),1->2(KH đã thanh toán xong)
//  4->5(muốn trả lại đồ), 7->8(KH đã thanh toán phí phạt hoặc không thì sẽ là xác nhận hoàn tất)
//  admin : 2->3->4, 5->6


  @Column(name = "created_at",nullable = false)
  @CreationTimestamp
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at",nullable = false)
  private LocalDateTime updatedAt;

  @ManyToOne
  @JoinColumn(name = "order_id",nullable = false, foreignKey = @ForeignKey(name = "FK_ORDER_ORDER-DETAIL"))
  private Order order;

  @ManyToOne
  @JoinColumn(name = "product_detail_id",nullable = false, foreignKey = @ForeignKey(name = "FK_PRODUCT-DETAIL_ORDER-DETAIL"))
  private ProductDetail productDetail;

  @JsonIgnore
  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "order_return_id", referencedColumnName = "id")
  private OrderReturn orderReturn;
}
