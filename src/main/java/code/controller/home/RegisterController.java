package code.controller.home;

import code.model.entity.User;
import code.model.request.RegisterRequest;
import code.security.JwtTokenUtil;
import code.service.customer.UserService;
import code.service.more.ImageService;
import java.io.IOException;
import org.apache.tomcat.util.http.fileupload.FileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;
// Import the required packages


@RestController
@RequestMapping("/api/home")
public class RegisterController {
  @Autowired
  private JwtTokenUtil jwtTokenUtil;

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private UserService userService;

  @Autowired
  private ImageService imageService;

  @Autowired
  public RegisterController(UserService userService) { // Constructor injection
    this.userService = userService;
  }
  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
    User newUser = this.userService.registerNewUser(registerRequest);
    String token = jwtTokenUtil.generateToken(newUser.getEmail(), "customer","Unknown",
        newUser.getId());
      Map<String, String> response = new HashMap<>();
      response.put("token", token);
    return ResponseEntity.ok(response);
  }
}
