package backend.repository;

import backend.model.Events;
import org.hibernate.query.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Events, UUID> {
    List<Events> findByCategoryIgnoreCase(String category);

    @Query(value = "SELECT * FROM events e WHERE e.tags @> ARRAY[LOWER(:tag)]::text[] AND e.is_public = true", nativeQuery = true)
    List<Events> findByTagsContainingIgnoreCase(String tag);

    @Query("SELECT DISTINCT e.category FROM Events e WHERE e.category IS NOT NULL AND e.isPublic = true")
    List<String> findDistinctCategories();

    @Query("SELECT DISTINCT unnest(e.tags) FROM Events e WHERE e.isPublic = true")
    List<String> findDistinctTags();

    @Query("SELECT e FROM Events e WHERE e.isPublic = true AND e.start >= :now ORDER BY e.start ASC")
    List<Events> findUpcomingEvents(LocalDateTime now);

    @Query("SELECT e FROM Events e WHERE e.userId = :userId")
    List<Events> findByUserId(UUID userId);



}