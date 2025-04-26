package backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "event")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    @Column(nullable = false)
    private String location;

    @Column(name = "available_seats", nullable = false)
    private Integer availableSeats;

    @Column(name = "participation_cost")
    private BigDecimal participationCost;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EventCategory category;

    public enum EventCategory {
        conference, seminar, workshop
    }
}