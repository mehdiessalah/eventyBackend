package backend.repository;

import backend.model.Event;
import backend.model.Event.EventCategory;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // Paginated queries
    @NotNull Page<Event> findAll(@NotNull Pageable pageable);
    Page<Event> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<Event> findByCategory(EventCategory category, Pageable pageable);
    Page<Event> findByEventDateBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
    Page<Event> findByEventDateAfter(LocalDateTime date, Pageable pageable);
    Page<Event> findByAvailableSeatsGreaterThan(int seats, Pageable pageable);

    // Non-paginated for specific use cases
    List<Event> findByTitleContainingIgnoreCase(String title);
    List<Event> findByCategory(EventCategory category);
    List<Event> findByEventDateAfter(LocalDateTime date);

    // Custom query for location
    @Query("SELECT e FROM Event e WHERE LOWER(e.location) LIKE LOWER(CONCAT('%', :location, '%'))")
    Page<Event> findByLocationContaining(String location, Pageable pageable);

    // Combined search across multiple fields
    @Query("SELECT e FROM Event e WHERE " +
            "LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.location) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Event> searchEvents(String keyword, Pageable pageable);
}