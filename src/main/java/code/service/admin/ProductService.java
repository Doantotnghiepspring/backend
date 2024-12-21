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
import org.springframework.http.ResponseEntity;
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

  private void validateFile(MultipartFile[] files) {
    if(files.length > 4){
      throw new BadRequestException("Số lượng file không được vượt quá 4");
    }
    for (MultipartFile file : files) {
      //Lấy tên file và đuôi mở rộng của file
      String originalFilename = file.getOriginalFilename();
      String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);

      //Kiểm tra xem file có đúng định dạng không
      if (!extension.equals("png") && !extension.equals("jpg")
          && !extension.equals("gif") && !extension.equals("webp")
          && !extension.equals("jpeg")) {
        throw new BadRequestException("Không hỗ trợ định dạng file này!");
      }

      if (file.getSize() > 1024 * 1024) {
        throw new BadRequestException(
            "File " + file.getOriginalFilename() + " vượt quá dung lượng tối đa 1MB");
      }
    }
  }

  public Page<Product> getProducts(int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    return this.productRepository.findAll(pageable);
  }

  public Product getProductById(long product_id) {
    return this.productRepository.findById(product_id)
        .orElseThrow(() -> new NotFoundException("Không tìm thấy product có id : " + product_id));
  }
  public Product createProduct(CreateProductRequest request) throws IOException {
    validateFile(new MultipartFile[]{request.getFile()});
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
  public Image updateProductThumbnail(long productId, MultipartFile file) throws IOException {
    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new NotFoundException("Không tìm thấy Product có id : " + productId));
    validateFile(new MultipartFile[]{file});
    Image image = product.getThumbnail();
    Map<String, Object> options = ObjectUtils.asMap(
        "public_id", String.valueOf(image.getId()),      // Đặt public ID cho ảnh
        "tags", "thumbnail",// Thêm các tag (danh sách thẻ)
        "transformation", new Transformation().width(512).height(512)
            .crop("pad").quality(100)
    );
    Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
    image.setUrl(uploadResult.get("url").toString());
    imageRepository.save(image);
    product.setThumbnail(image);
    productRepository.save(product);
    return image;
  }
  public Product addProductImage(long productId, MultipartFile[] files) throws IOException {
    validateFile(files);
    // Tìm kiếm sản phẩm
    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new NotFoundException("Không tìm thấy Product có id : " + productId));

    // Kiểm tra số lượng ảnh
    if (files.length + product.getImages().size() > 10) {
      throw new BadRequestException("Tổng số lượng ảnh không được quá 10 ảnh");
    }

    // Lặp qua từng file và upload ảnh
    for (MultipartFile file : files) {
      long uploadStartTime = System.currentTimeMillis();
      Image image = new Image();
      imageRepository.save(image);

      Map<String, Object> options = ObjectUtils.asMap(
          "public_id", String.valueOf(image.getId()),      // Đặt public ID cho ảnh
          "tags", "product-image",// Thêm các tag (danh sách thẻ)
          "transformation", new Transformation().width(512).height(512)
              .crop("pad").quality(60)
      );
      Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
      image.setUrl(uploadResult.get("url").toString());

      List<Image> images = product.getImages();
      images.add(image);
      product.setImages(images);
      productRepository.save(product);

      image.setProduct(product);
      imageRepository.save(image);
    }
    return productRepository.save(product);
  }
  public Image updateProductImage(long productId, long imageId, MultipartFile file) throws IOException {
    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new NotFoundException("Không tìm thấy Product có id : " + productId));
    Image image = imageRepository.findById(imageId)
        .orElseThrow(() -> new NotFoundException("Không tìm thấy Image có id : " + imageId));
    validateFile(new MultipartFile[]{file});

    Map<String, Object> options = ObjectUtils.asMap(
        "public_id", String.valueOf(image.getId()),      // Đặt public ID cho ảnh
        "tags", "thumbnail",// Thêm các tag (danh sách thẻ)
        "transformation", new Transformation().width(64).height(64)
            .crop("pad").quality(100)
    );
    Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
    image.setUrl(uploadResult.get("url").toString());
    imageRepository.save(image);
    productRepository.save(product);
    return image;
  }
  public Product deleteProductImage(long productId, long imageId) throws IOException{
    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new NotFoundException("Không tìm thấy Product có id : " + productId));
    Image image = imageRepository.findById(imageId)
        .orElseThrow(() -> new NotFoundException("Không tìm thấy Image có id : " + imageId));

//    Xóa ảnh trên cloud
    Map<?, ?> result = cloudinary.uploader().destroy(String.valueOf(imageId), ObjectUtils.emptyMap());

    if(product.getImages().contains(image)){
      List<Image> images = product.getImages();
      images.remove(image);
//      xóa thông tin ảnh trong database
      imageRepository.delete(image);
//      Cập nhật lại images trong product
      product.setImages(images);
      productRepository.save(product);
    }
    else{
      throw new BadRequestException("Product có id : "+productId+" không có Image có id : "+imageId);
    }
    return product;
  }

  public String updateStatusProduct(long productId,boolean status){
    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new NotFoundException("Không tìm thấy Product có id : " + productId));
    product.setStatus(status);
    productRepository.save(product);
    return "Cập nhật trang thái Product thành công";
  }
}
