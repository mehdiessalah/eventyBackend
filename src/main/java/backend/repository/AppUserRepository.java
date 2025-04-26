package backend.repository;

import backend.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface AppUserRepository extends JpaRepository<Users, UUID> {
    Optional<Users> findByEmail(String email);
}
