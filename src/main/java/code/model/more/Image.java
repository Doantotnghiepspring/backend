package code.model.more;

import code.model.entity.Category;
import code.model.entity.Product;
import code.model.entity.ProductDetail;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="images")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Image {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "url", nullable = false)
  private String url = "";

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "product_id")
  private Product product;

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "product_of_thumbnail_id")
  private Product productOfThumbnail;

  @JsonIgnore
  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "category_id", referencedColumnName = "id")
  private Category category;

  @JsonIgnore
  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "product_detail_id", referencedColumnName = "id")
  private ProductDetail productDetail;
}
