package code.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UpdateProductRequest {
  private long newCategoryId;
  private String newName;
  private  String newDescription;
  private  String newBrand;
}
