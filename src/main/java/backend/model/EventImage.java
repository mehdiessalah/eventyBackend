package backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Embeddable
@Data
public class EventImage {
    @NotNull
    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Size(max = 255)
    @Column(name = "caption")
    private String caption;

    @Column(name = "is_primary")
    private Boolean isPrimary;

    @Column(name = "order")
    private Integer order;
}