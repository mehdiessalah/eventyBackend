package backend.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "events")
public class Events {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Size(min = 1, max = 255)
    private String title;

    @Size(max = 1000)
    private String description;

    @NotNull
    private LocalDateTime start;

    @Column(name = "event_end")
    private LocalDateTime eventEnd;

    @Size(max = 255)
    private String location;

    private Boolean allDay;

    private Boolean draggable;

    @Size(max = 50)
    private String color;

    @Size(max = 50)
    private String category;

    @Size(max = 255)
    private String organizer;

    @Email
    @Size(max = 255)
    private String contactEmail;

    @ElementCollection
    @CollectionTable(name = "event_images", joinColumns = @JoinColumn(name = "event_id"))
    @Column(name = "image_url")
    private List<EventImage> images;

    @Size(max = 255)
    private String thumbnail;

    private Integer attendees;

    private Integer maxAttendees;

    private Boolean isPublic;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "tags", columnDefinition = "text[]")
    private List<String> tags;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Column(name = "user_id")
    private UUID userId; // New field for creator

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getStart() { return start; }
    public void setStart(LocalDateTime start) { this.start = start; }
    public LocalDateTime getEnd() { return eventEnd; }
    public void setEnd(LocalDateTime eventEnd) { this.eventEnd = eventEnd; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public Boolean getAllDay() { return allDay; }
    public void setAllDay(Boolean allDay) { this.allDay = allDay; }
    public Boolean getDraggable() { return draggable; }
    public void setDraggable(Boolean draggable) { this.draggable = draggable; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getOrganizer() { return organizer; }
    public void setOrganizer(String organizer) { this.organizer = organizer; }
    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    public List<EventImage> getImages() { return images; }
    public void setImages(List<EventImage> images) { this.images = images; }
    public String getThumbnail() { return thumbnail; }
    public void setThumbnail(String thumbnail) { this.thumbnail = thumbnail; }
    public Integer getAttendees() { return attendees; }
    public void setAttendees(Integer attendees) { this.attendees = attendees; }
    public Integer getMaxAttendees() { return maxAttendees; }
    public void setMaxAttendees(Integer maxAttendees) { this.maxAttendees = maxAttendees; }
    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}