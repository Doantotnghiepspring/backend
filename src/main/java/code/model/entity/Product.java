package code.model.entity;
import code.model.more.Image;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;
import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name="products")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Product {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name",nullable = false,length = 200,unique = true)
  private String name;

  @Column(name = "brand",nullable = false)
  private String brand;

  @Lob
  @Column(name = "description",nullable = false,columnDefinition = "TEXT")
  private String description;

  @Column(name = "slug", nullable = false, unique = true)
  private String slug;

  @Column(name = "status", nullable = false)
  private boolean status = true;

  // Quan hệ 1-1: Thumbnail (ảnh đại diện)
  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "thumbnail_id", referencedColumnName = "id")
  private Image thumbnail;

  // Quan hệ 1-N: Danh sách các ảnh khác
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "product")
  private List<Image> images;

  @Column(name = "created_at",nullable = false)
  @CreationTimestamp
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at",nullable = false)
  private LocalDateTime updatedAt;

  @ManyToOne
  @JoinColumn(name = "category_id",nullable = false, foreignKey = @ForeignKey(name = "FK_CATEGORY_PRODUCT"))
  private Category category;

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ProductDetail> productDetails;
}
