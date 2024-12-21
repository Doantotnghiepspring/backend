//package code.seeder;
//
//import code.model.entity.Category;
//import code.model.entity.Product;
//import code.model.entity.ProductDetail;
//import code.repository.*;
//import lombok.AllArgsConstructor;
//import lombok.NoArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//import org.springframework.beans.factory.annotation.Autowired;
//
//@Component
//@AllArgsConstructor
//public class ProductSeeder implements CommandLineRunner {
//
//  private ProductRepository productRepository;
//  private ProductDetailRepository productDetailRepository;
//  private CategoryRepository categoryRepository;
//
//  @Override
//  public void run(String... args) throws Exception {
//    // Kiểm tra nếu bảng User rỗng
//    if (categoryRepository.count() * productRepository.count() * productDetailRepository.count()
//        == 0) {
////      Taọ sẵn 1 danh mục mới là Điện thoại
//      Category category = new Category();
//      category.setName("Điện thoại");
//      categoryRepository.save(category);
//
////      Tạo sẵn 1 sản phẩm là Iphone 15
//      Product product = new Product();
//      product.setCategory(category);
//      product.setBrand("Apple");
//      product.setName("Iphone 15");
//      product.setSlug("iphone-15");
//      product.setDescription("iPhone 15 Pro Max đã chính thức được ra mắt trong sự kiện Wonderlust"
//          + " tại nhà hát Steve Jobs, California (Mỹ) vào lúc 10h sáng 12/9 tức 0h ngày 13/9 (giờ Việt Nam). "
//          + "Chiếc iPhone mới trong series mới đem đến cho người dùng trải nghiệm ổn định hơn với thế hệ chip A17 Pro mới nhất, "
//          + "cùng công nghệ Wi-Fi 6E. Bộ camera của iPhone 15 Pro Max cũng được nâng cấp thêm "
//          + "với ống kính tele với khả năng zoom quang học 5x với thiết kế tetraprism hiện đại.");
//      productRepository.save(product);
//
////      Tạo sẵn các thuộc tính sản phẩm đã có
//// ProductDetail 1
//      ProductDetail productDetail1 = new ProductDetail(product, "256G", true, 10000, 20,
//          "90% like new", "Titan trắng");
//      productDetailRepository.save(productDetail1);
//
//      ProductDetail productDetail2 = new ProductDetail(product, "128G", false, 9000, 15,
//          "85% like new", "Đen bóng");
//      productDetailRepository.save(productDetail2);
//
//      ProductDetail productDetail3 = new ProductDetail(product, "512G", true, 15000, 30,
//          "99% like new", "Vàng đồng");
//      productDetailRepository.save(productDetail3);
//
//      ProductDetail productDetail4 = new ProductDetail(product, "64G", false, 8000, 10,
//          "80% like new", "Xanh dương");
//      productDetailRepository.save(productDetail4);
//
//      ProductDetail productDetail5 = new ProductDetail(product, "1TB", true, 20000, 50,
//          "New", "Đỏ ánh kim");
//      productDetailRepository.save(productDetail5);
//
//      ProductDetail productDetail6 = new ProductDetail(product, "256G", true, 12000, 25,
//          "95% like new", "Bạc ánh kim");
//      productDetailRepository.save(productDetail6);
//
//      ProductDetail productDetail7 = new ProductDetail(product, "128G", false, 8500, 18,
//          "88% like new", "Đen nhám");
//      productDetailRepository.save(productDetail7);
//
//      ProductDetail productDetail8 = new ProductDetail(product, "64G", true, 7500, 12,
//          "70% like new", "Hồng phấn");
//      productDetailRepository.save(productDetail8);
//
//      ProductDetail productDetail9 = new ProductDetail(product, "512G", true, 14000, 28,
//          "92% like new", "Xanh lá");
//      productDetailRepository.save(productDetail9);
//
//      ProductDetail productDetail10 = new ProductDetail(product, "1TB", true, 22000, 60,
//          "New", "Bạc nguyên khối");
//      productDetailRepository.save(productDetail10);
//
//      System.out.println("Default productDetail created.");
//    }
//  }
//}
//
