package backend.service;

import backend.model.UserSubscription;
import backend.repository.EventRepository;
import backend.repository.UserSubscriptionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SubscriptionService {
    private final UserSubscriptionRepository userSubscriptionRepository;
    private final EventRepository eventRepository;
    private static final Logger logger = LoggerFactory.getLogger(SubscriptionService.class);

    @Autowired
    public SubscriptionService(UserSubscriptionRepository userSubscriptionRepository, EventRepository eventRepository) {
        this.userSubscriptionRepository = userSubscriptionRepository;
        this.eventRepository = eventRepository;
    }

    @Transactional(readOnly = true)
    public List<String> getSubscribedEventIds(UUID userId) {
        try {
            logger.debug("Fetching subscribed event IDs for user {}", userId);
            List<UserSubscription> subscriptions = userSubscriptionRepository.findByUserId(userId);
            List<String> eventIds = subscriptions.stream()
                    .map(sub -> sub.getEventId().toString())
                    .collect(Collectors.toList());
            logger.info("Found {} subscribed events for user {}", eventIds.size(), userId);
            return eventIds;
        } catch (Exception e) {
            logger.error("Failed to fetch subscribed event IDs for user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch subscriptions", e);
        }
    }

    @Transactional
    public void subscribeToEvent(UUID userId, UUID eventId) {
        try {
            logger.debug("Subscribing user {} to event {}", userId, eventId);
            if (!eventRepository.existsById(eventId)) {
                throw new EntityNotFoundException("Event not found with id: " + eventId);
            }
            if (userSubscriptionRepository.existsByUserIdAndEventId(userId, eventId)) {
                logger.info("User {} already subscribed to event {}", userId, eventId);
                return;
            }
            UserSubscription subscription = new UserSubscription();
            subscription.setUserId(userId);
            subscription.setEventId(eventId);
            subscription.setSubscribedAt(ZonedDateTime.now());
            userSubscriptionRepository.save(subscription);
            logger.info("Successfully subscribed user {} to event {}", userId, eventId);
        } catch (Exception e) {
            logger.error("Failed to subscribe user {} to event {}: {}", userId, eventId, e.getMessage(), e);
            throw new RuntimeException("Failed to subscribe to event", e);
        }
    }

    @Transactional
    public void unsubscribeFromEvent(UUID userId, UUID eventId) {
        try {
            logger.debug("Unsubscribing user {} from event {}", userId, eventId);
            if (!userSubscriptionRepository.existsByUserIdAndEventId(userId, eventId)) {
                logger.info("User {} not subscribed to event {}", userId, eventId);
                return;
            }
            userSubscriptionRepository.deleteByUserIdAndEventId(userId, eventId);
            logger.info("Successfully unsubscribed user {} from event {}", userId, eventId);
        } catch (Exception e) {
            logger.error("Failed to unsubscribe user {} from event {}: {}", userId, eventId, e.getMessage(), e);
            throw new RuntimeException("Failed to unsubscribe from event", e);
        }
    }
}