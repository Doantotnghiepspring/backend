package code.repository;

import code.model.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
  User findByEmail(String email);
  Optional<User> findByIdAndRole(long id,String role);

}
