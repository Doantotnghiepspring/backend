package code.controller.admin;

import code.exception.BadRequestException;
import code.model.entity.Category;
import code.service.admin.CategoryService;
import java.io.IOException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController("AdminCategoryController")
@RequestMapping("/api/admin")
public class CategoryController {
  private CategoryService categoryService;

  public CategoryController(CategoryService categoryService){
    this.categoryService = categoryService;
  }

  @GetMapping("/categories")
  public ResponseEntity<Page<Category>> getCategories(@RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size){
    return ResponseEntity.ok(this.categoryService.getCategories(page,size));
  }

  @PostMapping("/categories")
  public ResponseEntity<Category> createCategory(
      @RequestParam String categoryName,
      @RequestParam MultipartFile file)
      throws IOException {
    if(categoryName == null || categoryName.trim().equals("")){
       throw new BadRequestException("Tên danh mục không được để trống");
    }
    if (file == null || file.isEmpty()) {
      throw new BadRequestException("File ảnh trống");
    }
    return ResponseEntity.ok(this.categoryService.createCategory(categoryName,file));
  }

  @PutMapping("/categories/{categoryId}")
  public ResponseEntity<Category> updateCategory(
      @PathVariable long categoryId,
      @RequestParam String categoryName){
    return ResponseEntity.ok(this.categoryService.updateCategory(categoryId,categoryName));
  }

  @PutMapping("/categories/{categoryId}/image")
  public ResponseEntity<Category> updateImageCategory(
      @PathVariable long categoryId,
      @RequestParam MultipartFile file) throws IOException {
    if (file.isEmpty() || file == null) {
      throw new BadRequestException("File không được để trống");
    }
        return ResponseEntity.ok(categoryService.updateImageCategory(file,categoryId));
  }
}
