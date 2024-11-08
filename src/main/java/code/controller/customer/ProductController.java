package code.controller.customer;

import code.service.customer.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("customerProductController")
@RequestMapping("/api/customer")
public class ProductController {

  private ProductService productService;

  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  //  Lấy tất cả các product
  @GetMapping("/products")
  public ResponseEntity<?> getProducts() {
    return ResponseEntity.ok(this.productService.getProductDTOs(0, 10));
  }

  //  Lấy tất cả các product theo danh mục
  @GetMapping("/categories/{categoryId}/products")
  public ResponseEntity<?> getProductsByCategory(@PathVariable long categoryId) {
    return ResponseEntity.ok(this.productService.getProductDTOsByCategoryId(categoryId, 0, 10));
  }

  //  Lấy product có id là productId
  @GetMapping("/products/{productId}")
  public ResponseEntity<?> getTypesByProductId(@PathVariable long productId) {
    return ResponseEntity.ok(this.productService.getProductDTOByProductId(productId));
  }

  //Tìm kiếm sản phẩm
  @GetMapping("/products/search")
  public ResponseEntity<?> getProductDTOsByKeyword(@RequestParam String keyword) {
    return ResponseEntity.ok(this.productService.findProductDTOsByKeyword(keyword));
  }
}
