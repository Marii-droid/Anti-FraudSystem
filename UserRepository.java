package antifraud;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
        User findByUsername(String username);
        boolean existsByUsername(String username);
        List<User> findByOrderById();
        void deleteByUsername(String username);
}
