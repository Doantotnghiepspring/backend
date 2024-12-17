package code.repository;

import code.model.more.Notification;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification,Long> {

  Page<Notification> findByRoleReceiveAndUserId(String role,long userId, Pageable pageable);
}
