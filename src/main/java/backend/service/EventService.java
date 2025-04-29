package backend.service;

import backend.model.Events;
import backend.repository.EventRepository;
import backend.repository.UserSubscriptionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;
    private static final Logger logger = LoggerFactory.getLogger(EventService.class);

    @Autowired
    public EventService(EventRepository eventRepository, UserSubscriptionRepository userSubscriptionRepository) {
        this.eventRepository = eventRepository;
        this.userSubscriptionRepository = userSubscriptionRepository;
    }

    @Transactional(readOnly = true)
    public List<Events> getAllEvents() {
        try {
            logger.debug("Fetching all public events");
            List<Events> events = eventRepository.findAll().stream()
                    .filter(Events::getIsPublic)
                    .toList();
            logger.info("Retrieved {} public events", events.size());
            return events;
        } catch (Exception e) {
            logger.error("Failed to fetch events: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch events", e);
        }
    }

    @Transactional(readOnly = true)
    public Events getEventById(UUID id) {
        logger.debug("Fetching event with id: {}", id);
        try {
            return eventRepository.findById(id)
                    .filter(Events::getIsPublic)
                    .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + id));
        } catch (Exception e) {
            logger.error("Failed to fetch event with id {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch event", e);
        }
    }

    @Transactional
    public Events createEvent(Events event) {
        try {
            logger.debug("Creating event with title: {}, userId: {}", event.getTitle(), event.getUserId());
            if (event.getUserId() == null) {
                throw new IllegalArgumentException("user_id cannot be null");
            }
            event.setId(null); // Ensure new event
            event.setCreatedAt(LocalDateTime.now());
            event.setUpdatedAt(LocalDateTime.now());
            if (event.getTags() != null) {
                event.setTags(event.getTags().stream().map(String::toLowerCase).toList());
            }
            if (event.getImages() != null) {
                logger.debug("Processing {} images for event", event.getImages().size());
                event.getImages().forEach(image -> {
                    if (image.getImageUrl() == null || image.getImageUrl().isBlank()) {
                        throw new IllegalArgumentException("Image URL cannot be null or empty");
                    }
                });
            }
            Events savedEvent = eventRepository.save(event);
            logger.info("Successfully created event with ID: {}", savedEvent.getId());
            return savedEvent;
        } catch (Exception e) {
            logger.error("Failed to create event: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create event", e);
        }
    }

    @Transactional
    public Events updateEvent(UUID id, Events event) {
        logger.debug("Updating event with id: {}", id);
        try {
            Events existing = eventRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + id));
            existing.setTitle(event.getTitle());
            existing.setDescription(event.getDescription());
            existing.setStart(event.getStart());
            existing.setEnd(event.getEnd());
            existing.setLocation(event.getLocation());
            existing.setAllDay(event.getAllDay());
            existing.setDraggable(event.getDraggable());
            existing.setColor(event.getColor());
            existing.setCategory(event.getCategory());
            existing.setOrganizer(event.getOrganizer());
            existing.setContactEmail(event.getContactEmail());
            existing.setImages(event.getImages());
            existing.setThumbnail(event.getThumbnail());
            existing.setAttendees(event.getAttendees());
            existing.setMaxAttendees(event.getMaxAttendees());
            existing.setIsPublic(event.getIsPublic());
            existing.setTags(event.getTags());
            return eventRepository.save(existing);
        } catch (Exception e) {
            logger.error("Failed to update event with id {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to update event", e);
        }
    }

    @Transactional
    public void deleteEvent(UUID id) {
        logger.debug("Deleting event with id: {}", id);
        try {
            if (!eventRepository.existsById(id)) {
                throw new EntityNotFoundException("Event not found with id: " + id);
            }
            eventRepository.deleteById(id);
        } catch (Exception e) {
            logger.error("Failed to delete event with id {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to delete event", e);
        }
    }

    @Transactional
    public Events updateEventDates(UUID id, LocalDateTime start, LocalDateTime end) {
        logger.debug("Updating event dates for id: {}", id);
        try {
            Events event = eventRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + id));
            event.setStart(start);
            event.setEnd(end);
            return eventRepository.save(event);
        } catch (Exception e) {
            logger.error("Failed to update event dates for id {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to update event dates", e);
        }
    }

    @Transactional(readOnly = true)
    public List<Events> getEventsByCategory(String category) {
        logger.debug("Fetching events by category: {}", category);
        try {
            return eventRepository.findByCategoryIgnoreCase(category);
        } catch (Exception e) {
            logger.error("Failed to fetch events by category {}: {}", category, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch events by category", e);
        }
    }

    @Transactional(readOnly = true)
    public List<Events> getEventsByTag(String tag) {
        logger.debug("Fetching events by tag: {}", tag);
        try {
            return eventRepository.findByTagsContainingIgnoreCase(tag);
        } catch (Exception e) {
            logger.error("Failed to fetch events by tag {}: {}", tag, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch events by tag", e);
        }
    }

    @Transactional(readOnly = true)
    public List<Events> getUpcomingEvents(int limit) {
        logger.debug("Fetching up to {} upcoming events", limit);
        try {
            return eventRepository.findUpcomingEvents(LocalDateTime.now())
                    .stream()
                    .limit(limit)
                    .toList();
        } catch (Exception e) {
            logger.error("Failed to fetch upcoming events: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch upcoming events", e);
        }
    }

    @Transactional(readOnly = true)
    public List<Events> getEventsByUserId(UUID userId) {
        logger.debug("Fetching events by user id: {}", userId);
        try {
            List<Events> events = eventRepository.findByUserId(userId);
            logger.info("Found {} events for user {}", events.size(), userId);
            return events;
        } catch (Exception e) {
            logger.error("Failed to fetch events by user id {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch events by user id", e);
        }
    }
}