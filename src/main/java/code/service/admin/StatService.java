package code.service.admin;

import code.exception.NotFoundException;
import code.model.more.Transaction;
import code.repository.CategoryRepository;
import code.repository.OrderRepository;
import code.repository.ProductRepository;
import code.repository.TransactionRepository;
import code.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class StatService {
  private TransactionRepository transactionRepository;
  private UserRepository userRepository;
  private CategoryRepository categoryRepository;
  private OrderRepository orderRepository;
  private ProductRepository productRepository;

  public StatService(TransactionRepository transactionRepository,
      CategoryRepository categoryRepository,
      OrderRepository orderRepository,
      ProductRepository productRepository,
      UserRepository userRepository){
    this.transactionRepository = transactionRepository;
    this.userRepository = userRepository;
    this.categoryRepository = categoryRepository;
    this.orderRepository = orderRepository;
    this.productRepository = productRepository;
  }
//  Lấy toàn bộ các giao dịch
  public Page<Transaction> getTransactions(int page,int size){
    Pageable pageable = PageRequest.of(page, size);
    return transactionRepository.findAll(pageable);
  }

//  Xem chi tiết giao dịch
  public Transaction getTransactionById(long transactionId){
    return transactionRepository.findById(transactionId)
        .orElseThrow(()-> new NotFoundException("Không tìm thấy giao dịch tương ứng"));
  }

// Tìm các giao dịch trong khoảng thời gian
  public Page<Transaction> getTransactionsBetweenDates(LocalDateTime startDate, LocalDateTime endDate,int page,int size) {
    Pageable pageable = PageRequest.of(page, size);
    return transactionRepository.findByTransactionDateBetween(startDate, endDate,pageable);
  }

//  Thống kê : số lượng tài khoản, số lượng danh mục, số lượng mặt hàng(Product), số lượng đơn hàng(Order)
  public Map<String,Long> statOverall(){
    long totalUsers = userRepository.count();
    long totalCategories = categoryRepository.count();
    long totalProducts = productRepository.count();
    long totalOrders = orderRepository.count();
    Map<String,Long> map = new HashMap<>();
    map.put("totalCategories",totalCategories);
    map.put("totalUsers",totalUsers);
    map.put("totalProducts",totalProducts);
    map.put("totalOrders",totalOrders);
    return map;
  }
//  Thống kê doanh thu theo từng tháng của năm year
  public Map<String,Long> statByMonths(int year){
    Map<String,Long> map = new LinkedHashMap<>();
    for(int i = 1;i <= 12;i++){
      map.put("t" + i,transactionRepository.getTotalAmountByMonthYearAndX(i,year,1));
    }
    return map;
  }
}
