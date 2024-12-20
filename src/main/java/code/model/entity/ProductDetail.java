package code.model.entity;

import code.model.more.Image;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "product_details", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"type", "color","product_condition"})})
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ProductDetail {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "color", nullable = false)
  private String color;

  @Column(name = "type", nullable = false)
  private String type;

  //  giá thuê chung
  @Column(name = "price", nullable = false)
  private long price;

  @Column(name = "product_condition", nullable = false)
  private String condition;

  @Column(name = "inventory", nullable = false)
  private long inventory;

  @Column(name = "status", nullable = false)
  private boolean status = true;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "image_id", referencedColumnName = "id")
  private Image image;

  @Column(name = "created_at", nullable = false)
  @CreationTimestamp
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "product_id", nullable = false, foreignKey = @ForeignKey(name = "FK_PRODUCT_PRODUCT-DETAIL"))
  private Product product;

  @JsonIgnore
  @OneToMany(mappedBy = "productDetail", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<OrderDetail> orderDetails;

  @JsonIgnore
  @OneToMany(mappedBy = "productDetail", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Review> reviews;

  public ProductDetail(Product product, String type, boolean status, long price, long inventory,
      String condition, String color) {
    this.price = price;
    this.product = product;
    this.type = type;
    this.inventory = inventory;
    this.condition = condition;
    this.color = color;
    this.status = status;
  }
}
