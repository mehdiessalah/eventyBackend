package backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_subscriptions")
@Data
@IdClass(UserSubscriptionId.class)
public class UserSubscription {
    @Id
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Id
    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @Column(name = "subscribed_at", nullable = false)
    private ZonedDateTime subscribedAt = ZonedDateTime.now();
}