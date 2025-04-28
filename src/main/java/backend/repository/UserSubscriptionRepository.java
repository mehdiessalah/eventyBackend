package backend.repository;

import backend.model.UserSubscription;
import backend.model.UserSubscriptionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, UserSubscriptionId> {
    List<UserSubscription> findByUserId(UUID userId);

    boolean existsByUserIdAndEventId(UUID userId, UUID eventId);

    @Modifying
    @Query("DELETE FROM UserSubscription s WHERE s.userId = :userId AND s.eventId = :eventId")
    void deleteByUserIdAndEventId(UUID userId, UUID eventId);
}