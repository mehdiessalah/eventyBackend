package backend.model;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class UserSubscriptionId implements Serializable {
    private UUID userId;
    private UUID eventId;
}