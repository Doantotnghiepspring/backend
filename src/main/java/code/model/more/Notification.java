package code.model.more;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name="notifications")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Notification {
  @JsonIgnore
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

// Neu la khach hang thi la userId cua khach hang : id của người nhan
  @Column(name = "user_id", nullable = false)
  private long userReceiveId = 0;

  @Column(name = "order_id", nullable = false)
  private long orderId;

  @Column(name = "content", nullable = false)
  private String content;

  @Column(name = "role_receive", nullable = false)
  private String roleReceive;

//  Trang thai thong bao : 0 - chưa đọc thông báo; 1 - đã đọc thông báo
  @Column(name = "status", nullable = false)
  private boolean status;

////  Chủ để nội dung thông báo
//  @Column(name = "category", nullable = false)
//  private String category;

  @Column(name = "created_at",nullable = false)
  @CreationTimestamp
  private LocalDateTime createdAt;
}
