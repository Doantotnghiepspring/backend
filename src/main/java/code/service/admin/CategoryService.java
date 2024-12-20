package code.service.admin;

import code.model.entity.Category;
import code.exception.*;
import code.model.more.Image;
import code.repository.CategoryRepository;
import code.repository.ImageRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import java.io.IOException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

@Service("AdminCategoryService")
public class CategoryService {
  private CategoryRepository categoryRepository;
  private final Cloudinary cloudinary;
  private ImageRepository imageRepository;

  public CategoryService(CategoryRepository categoryRepository, Cloudinary cloudinary,
      ImageRepository imageRepository) {
    this.categoryRepository = categoryRepository;
    this.cloudinary = cloudinary;
    this.imageRepository = imageRepository;
  }

  public Page<Category> getCategories(int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    return this.categoryRepository.findAll(pageable);
  }

  public Category createCategory(String categoryName, MultipartFile file)
      throws IOException {

    //Lấy tên file và đuôi mở rộng của file
    String originalFilename = file.getOriginalFilename();
    String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);

    //Kiểm tra xem file có đúng định dạng không
    if (!extension.equals("png") && !extension.equals("jpg")
        && !extension.equals("gif") && !extension.equals("svg")
        && !extension.equals("jpeg")) {
      throw new BadRequestException("Không hỗ trợ định dạng file này!");
    }

    if (categoryRepository.findByName(categoryName).isPresent()) {
      throw new ConflictException("Tên danh mục đã tồn tại");
    } else {
//      Tạo đối tượng ảnh, lưu vào để lấy id đối tượng
      Image image = new Image();
      imageRepository.save(image);

      Map<String, Object> options = ObjectUtils.asMap(
          "public_id", String.valueOf(image.getId()),      // Đặt public ID cho ảnh
          "tags", "category",// Thêm các tag (danh sách thẻ)
          "transformation", new Transformation().width(64).height(64)
              .crop("pad").quality(100)
      );
      Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), options);

      Category category = new Category();
      category.setName(categoryName);
      category.setImage(image);
      categoryRepository.save(category);

      image.setUrl(uploadResult.get("url").toString());
      image.setCategory(category);
      imageRepository.save(image);
      return category;
    }
  }

  public Category updateCategory(long categoryId, String categoryName) {
//      check categoryName
    Category category = this.categoryRepository.findById(categoryId)
        .orElseThrow(() -> new NotFoundException("Không tồn tại danh mục có id : " + categoryId));

    if (categoryName.trim().length() == 0) {
      throw new BadRequestException("Tên danh mục không được để trống");
    }
    if (!categoryName.equals(category.getName()) && categoryRepository.findByName(categoryName)
        .isPresent()) {
      throw new ConflictException("Tên danh mục đã tồn tại");
    } else {
      category.setName(categoryName);
      categoryRepository.save(category);
      return category;
    }
  }

  public Category updateImageCategory(MultipartFile file,long categoryId) throws IOException {
    //Lấy tên file và đuôi mở rộng của file
    String originalFilename = file.getOriginalFilename();
    String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);

    //Kiểm tra xem file có đúng định dạng không
    if (!extension.equals("png") && !extension.equals("jpg")
        && !extension.equals("gif") && !extension.equals("svg")
        && !extension.equals("jpeg")) {
      throw new BadRequestException("Không hỗ trợ định dạng file này!");
    }

    Category category = categoryRepository.findById(categoryId)
        .orElseThrow(()-> new NotFoundException("Không tìm thấy Category có id :"+categoryId));

    Image image = category.getImage();

    Map<String, Object> options = ObjectUtils.asMap(
        "public_id", String.valueOf(image.getId()),      // Đặt public ID cho ảnh
        "tags", "category",// Thêm các tag (danh sách thẻ)
        "transformation", new Transformation().width(64).height(64)
            .crop("pad").quality(100)
    );
    Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), options);

    image.setUrl(uploadResult.get("url").toString());
    imageRepository.save(image);
    category.setImage(image);
    return categoryRepository.save(category);
  }
}
