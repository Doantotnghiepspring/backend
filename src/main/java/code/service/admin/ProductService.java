package code.service.admin;

import code.exception.*;
import code.model.entity.Category;
import code.model.entity.Product;
import code.model.more.Image;
import code.model.request.CreateProductRequest;
import code.model.request.UpdateProductRequest;
import code.repository.CategoryRepository;
import code.repository.ImageRepository;
import code.repository.ProductRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import java.io.File;
import java.io.IOException;
import java.text.Normalizer;
import java.time.Instant;
import java.util.regex.Pattern;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;
import java.io.IOException;
import java.time.Instant;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;

@Service("AdminProductService")
public class ProductService {
  @Value("${UPLOAD_DIR}")
  private String UPLOAD_DIR;
  private ProductRepository productRepository;
  private CategoryRepository categoryRepository;
  private final Cloudinary cloudinary;
  private ImageRepository imageRepository;

  public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository,
      Cloudinary cloudinary, ImageRepository imageRepository) {
    this.productRepository = productRepository;
    this.categoryRepository = categoryRepository;
    this.cloudinary = cloudinary;
    this.imageRepository = imageRepository;
  }

  private String createSlug(String name) {
    String baseSlug = toSlug(name);
    String uniqueSlug = baseSlug;
    int count = 1;

    // Kiểm tra sự tồn tại của slug trong database
    while (productRepository.existsBySlug(uniqueSlug)) {
      uniqueSlug = baseSlug + "-" + count;
      count++;
    }
    return uniqueSlug;
  }

  // Chuyển đổi name thành slug cơ bản
  private String toSlug(String input) {
    String nowhitespace = Pattern.compile("[\\s]").matcher(input).replaceAll("-");
    String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
    return Pattern.compile("[^\\w-]").matcher(normalized).replaceAll("")
        .toLowerCase(Locale.ENGLISH);
  }

  public Page<Product> getProducts(int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    return this.productRepository.findAll(pageable);
  }

  public Product getProductById(long product_id) {
    return this.productRepository.findById(product_id)
        .orElseThrow(() -> new NotFoundException("Không tìm thấy product có id : " + product_id));
  }

  public Product createProduct(CreateProductRequest request)
      throws IOException {
    if (this.productRepository.findByName(request.getName()).isPresent()) {
      throw new ConflictException("Tên sản phẩm đã tồn tại!");
    }

    Image image = new Image();
    imageRepository.save(image);

    Map<String, Object> options = ObjectUtils.asMap(
        "public_id", String.valueOf(image.getId()),      // Đặt public ID cho ảnh
        "tags", "thumbnail",// Thêm các tag (danh sách thẻ)
        "transformation", new Transformation().width(512).height(512)
            .crop("pad").quality(100)
    );
    Map<?, ?> uploadResult = cloudinary.uploader().upload(request.getFile().getBytes(), options);
    image.setUrl(uploadResult.get("url").toString());

    Product product = new Product();
    product.setName(request.getName());
    product.setBrand(request.getBrand());
    product.setDescription(request.getDescription());
    product.setCategory(this.categoryRepository.findById(request.getCategoryId())
        .orElseThrow(() -> new NotFoundException(
            "Không tìm thấy category có id : " + request.getCategoryId()))
    );
    String uniqueSlug = createSlug(request.getName());
    product.setSlug(uniqueSlug);
    product.setThumbnail(image);
    productRepository.save(product);
    image.setProductOfThumbnail(product);
    imageRepository.save(image);

    product.setThumbnail(image);
    return productRepository.save(product);
  }

  public Product updateProduct(UpdateProductRequest request, long productId) {
    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new NotFoundException("Không tìm thấy Product có id : " + productId));
    if (!request.getNewName().equals(product.getName()) &&
        productRepository.findByName(request.getNewName()).isPresent()) {
      throw new ConflictException("Tên Product đã tồn tại");
    }
    Category category = categoryRepository.findById(request.getNewCategoryId())
        .orElseThrow(() -> new NotFoundException(
            "Không tìm thấy Category có id : " + request.getNewCategoryId()));
    product.setDescription(request.getNewDescription());
    product.setBrand(request.getNewBrand());
    product.setName(request.getNewName());
    product.setCategory(category);
    String uniqueSlug = createSlug(request.getNewName());
    product.setSlug(uniqueSlug);
    return productRepository.save(product);
  }

  public Product updateProductThumbnail(long productId,MultipartFile file) throws IOException {
    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new NotFoundException("Không tìm thấy Product có id : " + productId));

    //Lấy tên file và đuôi mở rộng của file
    String originalFilename = file.getOriginalFilename();
    String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);

    //Kiểm tra xem file có đúng định dạng không
    if (!extension.equals("png") && !extension.equals("jpg")
        && !extension.equals("gif") && !extension.equals("svg")
        && !extension.equals("jpeg")) {
      throw new BadRequestException("Không hỗ trợ định dạng file này!");
    }
    Image image = product.getThumbnail();
    Map<String, Object> options = ObjectUtils.asMap(
        "public_id", String.valueOf(image.getId()),      // Đặt public ID cho ảnh
        "tags", "thumbnail",// Thêm các tag (danh sách thẻ)
        "transformation", new Transformation().width(64).height(64)
            .crop("pad").quality(100)
    );
    Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
    image.setUrl(uploadResult.get("url").toString());
    imageRepository.save(image);
    product.setThumbnail(image);
    productRepository.save(product);
    return product;
  }


  public Product addProductImage(long productId, MultipartFile[] files) throws IOException {
    // Ghi lại thời gian bắt đầu của phương thức
    long startTime = System.currentTimeMillis();
    System.out.println("Bắt đầu xử lý: " + Instant.now());

    // Tìm kiếm sản phẩm
    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new NotFoundException("Không tìm thấy Product có id : " + productId));

    // Kiểm tra số lượng ảnh
    if (files.length + product.getImages().size() > 10) {
      throw new BadRequestException("Tổng số lượng ảnh không được quá 10 ảnh");
    }

    // In ra thời gian sau khi kiểm tra số lượng ảnh
    long checkFileCountTime = System.currentTimeMillis();
    System.out.println("Kiểm tra số lượng ảnh mất thời gian: " + (checkFileCountTime - startTime) + " ms");

    // Lặp qua từng file và upload ảnh
    for (MultipartFile file : files) {
      long uploadStartTime = System.currentTimeMillis();
      Image image = new Image();
      imageRepository.save(image);

      File uploadDir = new File(UPLOAD_DIR);
      if (!uploadDir.exists()) {
        uploadDir.mkdirs(); // Tạo thư mục nếu chưa có
      }
      String originalFilename = file.getOriginalFilename();
      String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
      String fileName = "image" + String.valueOf(image.getId()) + "." + extension;
      Path filePath = Paths.get(UPLOAD_DIR, fileName);

      // Lưu file vào thư mục
      file.transferTo(filePath.toFile());

//      Map<String, Object> options = ObjectUtils.asMap(
//          "public_id", String.valueOf(image.getId()),      // Đặt public ID cho ảnh
//          "tags", "product-image", // Thêm các tag (danh sách thẻ)
//          "transformation", new Transformation().width(512).height(512)
//              .crop("pad").quality(100)
//      );
//      Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
//      image.setUrl(uploadResult.get("url").toString());

      image.setUrl("/images/" + filePath);
      List<Image> images = product.getImages();
      images.add(image);
      product.setImages(images);
      productRepository.save(product);

      image.setProduct(product);
      imageRepository.save(image);

      // In ra thời gian sau khi hoàn thành upload một ảnh
      long uploadEndTime = System.currentTimeMillis();
      System.out.println("Upload ảnh " + image.getId() + " mất thời gian: " + (uploadEndTime - uploadStartTime) + " ms");
    }

    // In ra tổng thời gian của phương thức
    long endTime = System.currentTimeMillis();
    System.out.println("Kết thúc xử lý: " + Instant.now());
    System.out.println("Tổng thời gian xử lý: " + (endTime - startTime) + " ms");

    return productRepository.save(product);
  }

}
