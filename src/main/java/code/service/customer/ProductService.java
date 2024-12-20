package code.service.customer;

import code.exception.BadRequestException;
import code.exception.NotFoundException;
import code.model.dto.ProductDTO;
import code.model.dto.ProductDetailDTO;
import code.model.entity.Category;
import code.model.entity.Product;
import code.model.entity.ProductDetail;
import code.repository.CategoryRepository;
import code.repository.ProductDetailRepository;
import code.repository.ProductRepository;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service("CustomerProductService")
public class ProductService {

  private ProductRepository productRepository;
  private ProductDetailRepository productDetailRepository;
  private CategoryRepository categoryRepository;

  public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository,
      ProductDetailRepository productDetailRepository) {
    this.productRepository = productRepository;
    this.categoryRepository = categoryRepository;
    this.productDetailRepository = productDetailRepository;
  }

  private List<ProductDTO> convert(List<Product> products) {
    List<ProductDTO> productDTOs = new ArrayList<>();
    for (Product product : products) {
      ProductDTO productDTO = new ProductDTO();
      BeanUtils.copyProperties(product, productDTO);
      productDTO.setCategory(product.getCategory());
//     set giá thuê min-max
      for (ProductDetail productDetail : product.getProductDetails()) {
        if (productDTO.getMaxPrice() < productDetail.getPrice()) {
          productDTO.setMaxPrice(productDetail.getPrice());
        }
        if (productDTO.getMinPrice() == 0) {
          productDTO.setMinPrice(productDetail.getPrice());
        }
        if (productDTO.getMinPrice() > productDetail.getPrice()) {
          productDTO.setMinPrice(productDetail.getPrice());
        }
      }
//      Tính số lượt đã thuê

//      Tính số sao đánh giá

      productDTOs.add(productDTO);
    }
    return productDTOs;
  }

  public Page<ProductDTO> getProductDTOs(int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    List<ProductDTO> productDTOs = this.convert(productRepository.findAll());

//    Chuyển List ProductDTO sang phân trang
    int start = (int) pageable.getOffset();
    int end = Math.min((start + pageable.getPageSize()), productDTOs.size());
    List<ProductDTO> paginatedDTOs = productDTOs.subList(start, end);

    // Trả về Page<ProductDTO> bằng cách sử dụng PageImpl
    return new PageImpl<>(paginatedDTOs, pageable, productDTOs.size());
  }

  //  Lấy product theo categoryId
  public Page<ProductDTO> getProductDTOsByCategoryId(long categoryId, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    Category category = categoryRepository.findById(categoryId)
        .orElseThrow(() -> new NotFoundException("Không thấy category có id : " + categoryId));
    List<ProductDTO> productDTOs = this.convert(productRepository.findByCategory(category));
//    Chuyển List ProductDTO sang phân trang
    int start = (int) pageable.getOffset();
    int end = Math.min((start + pageable.getPageSize()), productDTOs.size());
    List<ProductDTO> paginatedDTOs = productDTOs.subList(start, end);

    // Trả về Page<ProductDTO> bằng cách sử dụng PageImpl
    return new PageImpl<>(paginatedDTOs, pageable, productDTOs.size());
  }

  //  Lọc product theo cátegory_id, minPrice, maxPrice, brand
  public Page<ProductDTO> getProductDTOsByCategoryIdAndPriceAndBrand(long categoryId, int page,
      int size, int minPrice, int maxPrice, String brand) {
    Pageable pageable = PageRequest.of(page, size);
    Category category = categoryRepository.findById(categoryId)
        .orElseThrow(() -> new NotFoundException("Không thấy category có id : " + categoryId));
    List<Product> products = new ArrayList<>();
    if (brand == null) {
      products = productRepository.findByCategory(category);
    } else {
      products = productRepository.findByCategoryAndBrand(category, brand);
    }

    List<ProductDTO> productDTOs = this.convert(products);
    Iterator<ProductDTO> iterator = productDTOs.iterator();

    while (iterator.hasNext()) {
      ProductDTO productDTO = iterator.next();
      if (productDTO.getMaxPrice() < minPrice || productDTO.getMinPrice() > maxPrice
          || productDTO.getMaxPrice() == 0) {
        iterator.remove(); // Xóa phần tử an toàn
      }
    }

//    Chuyển List ProductDTO sang phân trang
    int start = (int) pageable.getOffset();
    int end = Math.min((start + pageable.getPageSize()), productDTOs.size());
    List<ProductDTO> paginatedDTOs = productDTOs.subList(start, end);

    // Trả về Page<ProductDTO> bằng cách sử dụng PageImpl
    return new PageImpl<>(paginatedDTOs, pageable, productDTOs.size());
  }

  //  Lấy Product có id là productId (có cả các thuộc tính : color, type ,....)
  public ProductDetailDTO getProductDTOByProductId(long productId) {
    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new NotFoundException("Không thấy Product có id : " + productId));
    ProductDTO productDTO = new ProductDTO();
    BeanUtils.copyProperties(product, productDTO);
    ProductDetailDTO productDetailDTO = new ProductDetailDTO();
    productDetailDTO.setProductDTO(productDTO);
    productDetailDTO.setProductDetails(productDetailRepository.findByProductId(productId));
    return productDetailDTO;
  }

  // Lấy sản phẩm theo brand và category_id
  public Page<ProductDTO> getProductDTOsByCategoryIdAndBrand(long categoryId, String brand,
      int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    Category category = categoryRepository.findById(categoryId)
        .orElseThrow(() -> new NotFoundException("Không thấy category có id : " + categoryId));
    List<ProductDTO> productDTOs = this.convert(
        productRepository.findByCategoryAndBrand(category, brand));
//    Chuyển List ProductDTO sang phân trang
    int start = (int) pageable.getOffset();
    int end = Math.min((start + pageable.getPageSize()), productDTOs.size());
    List<ProductDTO> paginatedDTOs = productDTOs.subList(start, end);

    // Trả về Page<ProductDTO> bằng cách sử dụng PageImpl
    return new PageImpl<>(paginatedDTOs, pageable, productDTOs.size());
  }

//  Tìm kiếm sản phẩm
//  public List<ProductDTO> findProductDTOsByKeyword(String keyword){
//    List<Product> products = productRepository.findByNameContainingIgnoreCase(keyword);
//    return this.convert(products);
//  }

  public Page<ProductDTO> findProductDTOsByKeyword(String keyword, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    List<ProductDTO> productDTOs = this.convert(
        productRepository.findByNameContainingIgnoreCase(keyword));

//    Chuyển List ProductDTO sang phân trang
    int start = (int) pageable.getOffset();
    int end = Math.min((start + pageable.getPageSize()), productDTOs.size());
    List<ProductDTO> paginatedDTOs = productDTOs.subList(start, end);

    // Trả về Page<ProductDTO> bằng cách sử dụng PageImpl
    return new PageImpl<>(paginatedDTOs, pageable, productDTOs.size());
  }

//  Lấy product thoeo slug
public ProductDetailDTO getProductDTOBySlug(String slug) {
  Product product = productRepository.findBySlug(slug)
      .orElseThrow(() -> new NotFoundException("Không thấy Product có slug : " + slug));
  ProductDTO productDTO = new ProductDTO();
  BeanUtils.copyProperties(product, productDTO);
  ProductDetailDTO productDetailDTO = new ProductDetailDTO();
  productDetailDTO.setProductDTO(productDTO);
  productDetailDTO.setProductDetails(productDetailRepository.findByProductId(product.getId()));
  return productDetailDTO;
}
}
