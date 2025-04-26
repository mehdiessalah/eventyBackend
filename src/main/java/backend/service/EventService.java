package backend.service;

import backend.exception.InvalidEventException;
import backend.exception.ResourceNotFoundException;
import backend.model.Event;
import backend.model.Event.EventCategory;
import backend.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EventService {
    private static final Logger logger = LoggerFactory.getLogger(EventService.class);
    private final EventRepository eventRepository;

    // CREATE
    @Transactional
    public Event createEvent(Event event) {
        validateEvent(event);
        Event savedEvent = eventRepository.save(event);
        logger.info("Created event with ID: {}", savedEvent.getId());
        return savedEvent;
    }

    // READ
    public Event getEvent(Long id) {
        if (id == null) {
            throw new InvalidEventException("Event ID cannot be null");
        }
        return eventRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Event not found with ID: {}", id);
                    return new ResourceNotFoundException("Event not found with ID: " + id);
                });
    }

    public Page<Event> getAllEvents(Pageable pageable) {
        logger.debug("Fetching all events with pageable: {}", pageable);
        return eventRepository.findAll(pageable);
    }

    // UPDATE
    @Transactional
    public Event updateEvent(Long id, Event updatedEvent) {
        if (id == null || updatedEvent == null) {
            throw new InvalidEventException("Event ID or data cannot be null");
        }
        validateEvent(updatedEvent);
        Event existing = getEvent(id);
        existing.setTitle(updatedEvent.getTitle());
        existing.setDescription(updatedEvent.getDescription());
        existing.setEventDate(updatedEvent.getEventDate());
        existing.setLocation(updatedEvent.getLocation());
        existing.setAvailableSeats(updatedEvent.getAvailableSeats());
        existing.setParticipationCost(updatedEvent.getParticipationCost());
        existing.setCategory(updatedEvent.getCategory());
        Event savedEvent = eventRepository.save(existing);
        logger.info("Updated event with ID: {}", id);
        return savedEvent;
    }

    // DELETE
    @Transactional
    public void deleteEvent(Long id) {
        if (id == null) {
            throw new InvalidEventException("Event ID cannot be null");
        }
        if (!eventRepository.existsById(id)) {
            logger.error("Cannot delete non-existent event with ID: {}", id);
            throw new ResourceNotFoundException("Event not found with ID: " + id);
        }
        eventRepository.deleteById(id);
        logger.info("Deleted event with ID: {}", id);
    }

    // CUSTOM QUERIES
    public Page<Event> searchEvents(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            logger.debug("Empty keyword, returning all events");
            return getAllEvents(pageable);
        }
        logger.debug("Searching events with keyword: {}", keyword);
        return eventRepository.searchEvents(keyword.trim(), pageable);
    }

    public Page<Event> getUpcomingEvents(Pageable pageable) {
        logger.debug("Fetching upcoming events");
        return eventRepository.findByEventDateAfter(LocalDateTime.now(), pageable);
    }

    public Page<Event> getEventsByCategory(EventCategory category, Pageable pageable) {
        if (category == null) {
            throw new InvalidEventException("Category cannot be null");
        }
        logger.debug("Fetching events by category: {}", category);
        return eventRepository.findByCategory(category, pageable);
    }

    // Validation logic
    private void validateEvent(Event event) {
        if (event == null) {
            throw new InvalidEventException("Event cannot be null");
        }
        if (event.getTitle() == null || event.getTitle().trim().isEmpty()) {
            throw new InvalidEventException("Event title is required");
        }
        if (event.getDescription() == null || event.getDescription().trim().isEmpty()) {
            throw new InvalidEventException("Event description is required");
        }
        if (event.getEventDate() == null || event.getEventDate().isBefore(LocalDateTime.now())) {
            throw new InvalidEventException("Event date must be in the future");
        }
        if (event.getLocation() == null || event.getLocation().trim().isEmpty()) {
            throw new InvalidEventException("Event location is required");
        }
        if (event.getAvailableSeats() == null || event.getAvailableSeats() < 0) {
            throw new InvalidEventException("Available seats must be non-negative");
        }
        if (event.getCategory() == null) {
            throw new InvalidEventException("Event category is required");
        }
    }
}