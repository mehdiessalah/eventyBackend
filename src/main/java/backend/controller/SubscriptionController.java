package backend.controller;

import backend.service.SubscriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "http://localhost:4200", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE})
public class SubscriptionController {
    private final SubscriptionService subscriptionService;
    private static final Logger logger = LoggerFactory.getLogger(SubscriptionController.class);

    @Autowired
    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping("/subscriptions")
    public ResponseEntity<List<String>> getSubscribedEventIds(@RequestParam("userId") String userId) {
        try {
            logger.debug("Handling GET /api/dashboard/subscriptions?userId={}", userId);
            UUID uuidUserId = UUID.fromString(userId);
            List<String> eventIds = subscriptionService.getSubscribedEventIds(uuidUserId);
            logger.info("Returning {} subscribed event IDs for user {}", eventIds.size(), userId);
            return ResponseEntity.ok(eventIds);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid user ID {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(List.of());
        } catch (Exception e) {
            logger.error("Error in GET /api/dashboard/subscriptions?userId={}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of());
        }
    }

    @PostMapping("/subscriptions/{eventId}")
    public ResponseEntity<Void> subscribeToEvent(
            @PathVariable String eventId,
            @RequestBody SubscriptionRequest request) {
        try {
            logger.debug("Handling POST /api/dashboard/subscriptions/{} for user {}", eventId, request.getUserId());
            UUID uuidUserId = UUID.fromString(request.getUserId());
            UUID uuidEventId = UUID.fromString(eventId);
            subscriptionService.subscribeToEvent(uuidUserId, uuidEventId);
            logger.info("User {} subscribed to event {}", request.getUserId(), eventId);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException e) {
            logger.error("Invalid ID (eventId={}, userId={}): {}", eventId, request.getUserId(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            logger.error("Error in POST /api/dashboard/subscriptions/{} for user {}: {}", eventId, request.getUserId(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/subscriptions/{eventId}")
    public ResponseEntity<Void> unsubscribeFromEvent(
            @PathVariable String eventId,
            @RequestBody SubscriptionRequest request) {
        try {
            logger.debug("Handling DELETE /api/dashboard/subscriptions/{} for user {}", eventId, request.getUserId());
            UUID uuidUserId = UUID.fromString(request.getUserId());
            UUID uuidEventId = UUID.fromString(eventId);
            subscriptionService.unsubscribeFromEvent(uuidUserId, uuidEventId);
            logger.info("User {} unsubscribed from event {}", request.getUserId(), eventId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (IllegalArgumentException e) {
            logger.error("Invalid ID (eventId={}, userId={}): {}", eventId, request.getUserId(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            logger.error("Error in DELETE /api/dashboard/subscriptions/{} for user {}: {}", eventId, request.getUserId(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

class SubscriptionRequest {
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
