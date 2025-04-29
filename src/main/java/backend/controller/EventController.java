package backend.controller;

import backend.model.Events;
import backend.service.EventService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class EventController {
    private final EventService eventService;
    private static final Logger logger = LoggerFactory.getLogger(EventController.class);

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/events")
    public ResponseEntity<List<Events>> getAllEvents() {
        try {
            logger.debug("Handling GET /api/events");
            List<Events> events = eventService.getAllEvents();
            logger.info("Returning {} events", events.size());
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            logger.error("Error in GET /api/events: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(List.of());
        }
    }

    @GetMapping("/events/{id}")
    public ResponseEntity<Events> getEvent(@PathVariable UUID id) {
        try {
            logger.debug("Handling GET /api/events/{}", id);
            return ResponseEntity.ok(eventService.getEventById(id));
        } catch (Exception e) {
            logger.error("Error in GET /api/events/{}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/events")
    public ResponseEntity<Events> createEvent(@Valid @RequestBody Events event) {
        try {
            logger.debug("Handling POST /api/events with title: {}, userId: {}", event.getTitle(), event.getUserId());
            try {
                UUID.fromString(event.getUserId().toString()); // Validate UUID format
            } catch (IllegalArgumentException e) {
                logger.error("Invalid UUID format for userId: {}", event.getUserId());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            Events createdEvent = eventService.createEvent(event);
            logger.info("Created event with ID: {}", createdEvent.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
        } catch (Exception e) {
            logger.error("Error in POST /api/events: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/events/{id}")
    public ResponseEntity<Events> updateEvent(@PathVariable UUID id, @Valid @RequestBody Events event) {
        try {
            logger.debug("Handling PUT /api/events/{}", id);
            return ResponseEntity.ok(eventService.updateEvent(id, event));
        } catch (Exception e) {
            logger.error("Error in PUT /api/events/{}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable UUID id) {
        try {
            logger.debug("Handling DELETE /api/events/{}", id);
            eventService.deleteEvent(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            logger.error("Error in DELETE /api/events/{}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PatchMapping("/events/{id}/dates")
    public ResponseEntity<Events> updateEventDates(@PathVariable UUID id, @RequestBody Map<String, LocalDateTime> dates) {
        try {
            logger.debug("Handling PATCH /api/events/{}/dates", id);
            LocalDateTime start = dates.get("start");
            LocalDateTime end = dates.get("end");
            if (start == null) {
                throw new IllegalArgumentException("Start date is required");
            }
            return ResponseEntity.ok(eventService.updateEventDates(id, start, end));
        } catch (Exception e) {
            logger.error("Error in PATCH /api/events/{}/dates: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/events/category/{category}")
    public ResponseEntity<List<Events>> getEventsByCategory(@PathVariable String category) {
        try {
            logger.debug("Handling GET /api/events/category/{}", category);
            return ResponseEntity.ok(eventService.getEventsByCategory(category));
        } catch (Exception e) {
            logger.error("Error in GET /api/events/category/{}: {}", category, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(List.of());
        }
    }

    @GetMapping("/events/tag/{tag}")
    public ResponseEntity<List<Events>> getEventsByTag(@PathVariable String tag) {
        try {
            logger.debug("Handling GET /api/events/tag/{}", tag);
            return ResponseEntity.ok(eventService.getEventsByTag(tag));
        } catch (Exception e) {
            logger.error("Error in GET /api/events/tag/{}: {}", tag, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(List.of());
        }
    }

    @GetMapping("/events/upcoming")
    public ResponseEntity<List<Events>> getUpcomingEvents(@RequestParam(defaultValue = "5") int limit) {
        try {
            logger.debug("Handling GET /api/events/upcoming with limit: {}", limit);
            return ResponseEntity.ok(eventService.getUpcomingEvents(limit));
        } catch (Exception e) {
            logger.error("Error in GET /api/events/upcoming: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(List.of());
        }
    }

    @GetMapping("/events/by-user")
    public ResponseEntity<List<Events>> getEventsByUserId(@RequestParam("userId") String userId) {
        try {
            logger.debug("Handling GET /api/events/by-user with userId: {}", userId);
            UUID uuidUserId = UUID.fromString(userId);
            List<Events> events = eventService.getEventsByUserId(uuidUserId);
            logger.info("Returning {} events for user {}", events.size(), userId);
            return ResponseEntity.ok(events);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid user ID {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(List.of());
        } catch (Exception e) {
            logger.error("Error in GET /api/events/by-user: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of());
        }
    }

    private UUID getCurrentUserId(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        return UUID.fromString(jwt.getClaim("sub"));
    }
}