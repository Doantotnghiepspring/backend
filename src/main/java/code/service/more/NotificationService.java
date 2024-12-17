package code.service.more;

import code.exception.NotFoundException;
import code.model.more.Notification;
import code.repository.NotificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
  private NotificationRepository notificationRepository;
  public NotificationService(NotificationRepository notificationRepository){
    this.notificationRepository = notificationRepository;
  }

//  Tạo thông báo mới
  public Notification newCustomerNotification(long userId,long orderId,String content){
      Notification notification = new Notification();
      notification.setContent(content);
      if(userId == 0){
        notification.setRoleReceive("admin");
      }
      else{
        notification.setRoleReceive("customer");
      }
      notification.setUserReceiveId(userId);
      notification.setStatus(false);
      notification.setOrderId(orderId);
      return notificationRepository.save(notification);
  }

//Xem cac page thông báo
  public Page<Notification> getNotifications(String role,long userId,int page,int size){
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
    return notificationRepository.findByRoleReceiveAndUserReceiveId(role,userId,pageable);
  }

//  Danh dau thong bao da duoc xem
  public Notification seenNotification(long notificationId){
    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(()-> new NotFoundException("Không tìm thấy Notification có id : "+notificationId));
    notification.setStatus(true);
    return notificationRepository.save(notification);
  }
}
