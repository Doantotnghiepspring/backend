package code.controller.admin;

import code.exception.BadRequestException;
import code.model.entity.Product;
import code.model.request.CreateProductRequest;
import code.model.request.UpdateProductRequest;
import code.service.admin.CategoryService;
import code.service.admin.ProductService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController("adminProductController")
@RequestMapping("/api/admin")
public class ProductController {
  // Danh sách các định dạng ảnh hợp lệ
  private static final List<String> VALID_IMAGE_TYPES = Arrays.asList(
      "image/jpeg", "image/png", "image/gif"
  );
  private ProductService productService;

  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  @GetMapping("/products")
  public ResponseEntity<?> getProducts(@RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    return ResponseEntity.ok(this.productService.getProducts(page, size));
  }

  @GetMapping("/products/{productId}")
  public ResponseEntity<Product> getProductById(@PathVariable long productId) {
    return ResponseEntity.ok(this.productService.getProductById(productId));
  }

  //  Tạo Product mới
  @PostMapping("/products")
  public ResponseEntity<?> createProduct(
      @Valid @ModelAttribute CreateProductRequest request) throws IOException {

    if (request.getFile() == null || request.getFile().isEmpty()) {
      throw new BadRequestException("File ảnh trống");
    }
    return ResponseEntity.ok(this.productService.createProduct(request));
  }

  //  sửa đổi thông tin Product
  @PutMapping("/products/{productId}")
  public ResponseEntity<?> updateProduct(
      @PathVariable long productId,
      @Valid @RequestBody UpdateProductRequest request) throws IOException {
    return ResponseEntity.ok(productService.updateProduct(request, productId));
  }

//  Thay đổi thumbnail cho Product
  @PutMapping("/products/{productId}/thumbnail")
  public ResponseEntity<?> updateProductThumbnail(
      @PathVariable long productId,
      @RequestParam MultipartFile file) throws IOException {
    if (file == null || file.isEmpty()) {
      throw new BadRequestException("File ảnh trống");
    }
    return ResponseEntity.ok(productService.updateProductThumbnail(productId,file));
  }

//  Thêm list ảnh cho Product
//  Tạo Product mới
@PostMapping("/products/{productId}/images")
public ResponseEntity<?> createProductImages(
    @PathVariable long productId,
    @RequestParam MultipartFile[] files) throws IOException {

  if (files == null || files.length == 0 ) {
    throw new BadRequestException("File ảnh trống");
  }
  int maxFiles = 5; // Số lượng file tối đa
  long maxFileSize = 1 * 1024 * 1024; // 1MB mỗi file

  // Kiểm tra số lượng file
  if (files.length > maxFiles) {
    return ResponseEntity.badRequest().body("Số lượng file không được vượt quá " + maxFiles);
  }

  // Kiểm tra dung lượng từng file
  for (MultipartFile file : files) {
    if (file.getSize() > maxFileSize) {
      return ResponseEntity.badRequest()
          .body("File " + file.getOriginalFilename() + " vượt quá dung lượng tối đa 1MB");
    }
    // Kiểm tra định dạng file
    String contentType = file.getContentType();
    if (contentType == null || !VALID_IMAGE_TYPES.contains(contentType)) {
      return ResponseEntity.badRequest()
          .body("File " + file.getOriginalFilename() + " không phải là file ảnh hợp lệ");
    }
  }
  return ResponseEntity.ok(productService.addProductImage(productId,files));
}
}
