package code.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CreateProductRequest {
  @NotBlank(message = "Brand không được để trống")
  private String brand;
  @NotBlank(message = "Description không được để trống")
  private String description;
  private  String name;
  private long categoryId;
  private MultipartFile file;
}
