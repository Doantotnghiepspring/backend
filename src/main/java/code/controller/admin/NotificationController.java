package code.controller.admin;

import code.service.more.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("AdminNotificationController")
@RequestMapping("/api/admin/notification")
public class NotificationController {
  private NotificationService notificationService;
  public NotificationController(NotificationService notificationService){
    this.notificationService = notificationService;
  }

//  Xem tất cả page thông báo
  @GetMapping("/")
  public ResponseEntity<?> seeNotifications(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size){
    return ResponseEntity.ok(notificationService.getNotifications("admin",0,page,size));
  }

//  Đánh dấu đã xem 1 thông báo
  @PutMapping("/{notificationId}")
  public ResponseEntity<?> seenNotification(@PathVariable long notificationId){
      return ResponseEntity.ok(notificationService.seenNotification(notificationId));
  }
}
