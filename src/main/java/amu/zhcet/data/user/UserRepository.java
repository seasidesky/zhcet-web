package amu.zhcet.data.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    interface Identifier {
        String getUserId();
    }

    Optional<User> findByUserId(String userId);

    Optional<User> findByEmail(String email);

    List<Identifier> getByUserIdIn(List<String> userIds);

}
