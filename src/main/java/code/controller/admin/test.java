package code.controller.admin;

import code.security.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class test {
  @GetMapping("/api/admin/test")
  public ResponseEntity<?> get(
      @AuthenticationPrincipal CustomUserDetails userDetails
  ){
    return ResponseEntity.ok(userDetails.getUser());
  }

  @GetMapping("/api/customer/test")
  public ResponseEntity<?>  getCustomer(
      @AuthenticationPrincipal CustomUserDetails userDetails
  ){
    return ResponseEntity.ok(userDetails);
  }

  @PutMapping("/api/customer/test")
  public ResponseEntity<?>  getCusdsgdtomer(
      @AuthenticationPrincipal CustomUserDetails userDetails
  ){
    return ResponseEntity.ok(userDetails);
  }


//  dsjbgkslgklekjlnekl

}
