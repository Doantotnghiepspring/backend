package code.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name="orders")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Order {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "shipment",nullable = false)
  private String shipment;

  @Column(name = "payment",nullable = false)
  private String payment;

  @Column(name = "current_phone",nullable = false)
  private String currentPhone;

  @Column(name = "current_address",nullable = false)
  private String currentAddress;

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "user_id",nullable = false, foreignKey = @ForeignKey(name = "FK_USER_ORDER"))
  private User user;

  @JsonIgnore
  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<OrderDetail> orderDetails;
}
