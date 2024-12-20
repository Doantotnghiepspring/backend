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
  public ResponseEntity<?> getProducts(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    return ResponseEntity.ok(this.productService.getProductDTOs(page, size));
  }

  //  Lấy tất cả các product theo danh mục
  @GetMapping("/categories/{categoryId}/products")
  public ResponseEntity<?> getProductsByCategory(
      @PathVariable long categoryId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    return ResponseEntity.ok(
        this.productService.getProductDTOsByCategoryId(categoryId, page, size));
  }

  //  Lấy tất cả các product theo danh mục và brand
  @GetMapping("/categories/{categoryId}/products/")
  public ResponseEntity<?> getProductsByCategoryAndBrand(
      @PathVariable long categoryId,
      @RequestParam String brand,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    return ResponseEntity.ok(
        this.productService.getProductDTOsByCategoryId(categoryId, page, size));
  }

  //  Lọc sản phẩm dựa trên danh mục, giá và brand
  @GetMapping("/categories/{categoryId}/products/filter/")
  public ResponseEntity<?> getProductsByCategoryAndBrandAndPrice(
      @PathVariable long categoryId,
      @RequestParam(required = false) String brand,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "0", required = false) int minPrice,
      @RequestParam(defaultValue = "999999999", required = false) int maxPrice) {
    return ResponseEntity.ok(
        this.productService.getProductDTOsByCategoryIdAndPriceAndBrand(categoryId, page, size,
            minPrice, maxPrice, brand));
  }

  //  Lấy product có id là productId
  @GetMapping("/products/{productId}")
  public ResponseEntity<?> getTypesByProductId(@PathVariable long productId) {
    return ResponseEntity.ok(this.productService.getProductDTOByProductId(productId));
  }

  //Tìm kiếm sản phẩm
  @GetMapping("/products/search")
  public ResponseEntity<?> getProductDTOsByKeyword(@RequestParam String keyword,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    return ResponseEntity.ok(this.productService.findProductDTOsByKeyword(keyword, page, size));
  }

  //  Lấy sản phẩm theo slug
  @GetMapping("/{slug}")
  public ResponseEntity<?> getProductBySlug(@PathVariable String slug) {
    return ResponseEntity.ok(this.productService.getProductDTOBySlug(slug));
  }


}
