package code.service.customer;

import code.model.entity.User;
import code.exception.NotFoundException;
import code.model.dto.CustomerInfoDTO;
import code.model.more.Conversation;
import code.model.request.ChangePasswordRequest;
import code.model.request.RegisterRequest;
import code.model.request.UpdateCustomerRequest;
import code.repository.ConversationRepository;
import code.repository.UserRepository;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service("customerUserService")
public class UserService {
  private UserRepository userRepository;
  private PasswordEncoder passwordEncoder;
  private ConversationRepository conversationRepository;

  public UserService(UserRepository userRepository,PasswordEncoder passwordEncoder,ConversationRepository conversationRepository){
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.conversationRepository = conversationRepository;
  }

  public void checkIdCustomer(Long user_id){
    Optional<User> optionalUser = this.userRepository.findById(user_id);
    if (!optionalUser.isPresent()) {
      throw new NotFoundException("Không tìm thấy người dùng với id: " + user_id);
    }
  }
  public User registerNewUser(RegisterRequest registrationRequest) {
    // Kiểm tra xem email đã tồn tại hay chưa
    if (userRepository.findByEmail(registrationRequest.getEmail()) != null) {
      throw new RuntimeException("Email đã được sử dụng.");
    }

    // Kiểm tra định dạng email hợp lệ
    String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    if (!registrationRequest.getEmail().matches(emailRegex)) {
      throw new RuntimeException("Email không hợp lệ.");
    }
    // Kiểm tra độ dài mật khẩu
    if(registrationRequest.getPassword().length() < 6){
      throw new RuntimeException("Mật khẩu phải dài hơn 6 kí tự.");
    }
    // Tạo người dùng mới
    User newUser = new User();
    newUser.setEmail(registrationRequest.getEmail());
    newUser.setPassword(passwordEncoder.encode(registrationRequest.getPassword())); // Mã hóa mật khẩu
    newUser.setRole(registrationRequest.getRole());
    newUser.setName(registrationRequest.getName());
    newUser.setStatus(true);

    if(registrationRequest.getRole().equals("customer")){
      //    Tạo conversation mới rỗng
      Conversation conversation = new Conversation();
      conversation.setCustomerId(newUser.getId());
      conversation.setEmail(newUser.getEmail());
      conversationRepository.save(conversation);
    }

    // Lưu vào cơ sở dữ liệu
    return userRepository.save(newUser);

  }
  public User findByEmail(String email){
    return this.userRepository.findByEmail(email);
  }

  public CustomerInfoDTO getCustomerInfo(Long user_id) {
    CustomerInfoDTO customerInfoDTO = new CustomerInfoDTO(); // Khởi tạo DTO ở đây
    Optional<User> optionalUser = this.userRepository.findById(user_id);
    this.checkIdCustomer(user_id);
    // Lấy User từ Optional
    User user = optionalUser.get();
    customerInfoDTO.setId(user.getId());
    customerInfoDTO.setName(Objects.requireNonNullElse(user.getName(), "Chưa đặt tên"));
    customerInfoDTO.setPhone(Objects.requireNonNullElse(user.getPhone(), "Chưa có sdt"));
    customerInfoDTO.setEmail(user.getEmail());
    customerInfoDTO.setStatus(user.isStatus());
    return customerInfoDTO; // Trả về DTO nếu không có lỗi
  }

  public CustomerInfoDTO updateCustomerInfo(UpdateCustomerRequest request,User user){
    if(request.getName().trim().length() < 4){
      throw new RuntimeException("Tên quá ngắn.");
    }
    if (!request.getPhone().matches("^0[0-9]{9,10}$")) {
      throw new RuntimeException("Số điện thoại không hợp lệ.");
    }

    user.setName(request.getName());
    user.setPhone(request.getPhone());
    userRepository.save(user);
    return this.getCustomerInfo(user.getId());
  }

  public String changePassword(ChangePasswordRequest request,User user){
    // So sánh mật khẩu cũ với mật khẩu đã mã hóa
    if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
      throw new RuntimeException("Mật khẩu cũ không đúng.");
    }

    if(request.getNewPassword().length() < 6){
      throw new RuntimeException("Mật khẩu quá ngắn.");
    }
    // Mã hóa mật khẩu mới
    String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());

    // Cập nhật mật khẩu mới cho người dùng
    user.setPassword(encodedNewPassword);

    // Lưu người dùng với mật khẩu mới
    userRepository.save(user);

    return "Đổi mật khẩu thành công.";
  }
}
