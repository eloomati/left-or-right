package io.mhetko.lor.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

import io.mhetko.lor.entity.enums.ProposedTopicSource;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "proposed_topic")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProposedTopic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 5, max = 255)
    private String title;

    @Size(min = 50, max = 1000)
    private String description;

    @Column
    @Enumerated(EnumType.STRING)
    private ProposedTopicSource source;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "popularity_score", nullable = false)
    private Integer popularityScore = 0;

    @ManyToOne
    @JoinColumn(name = "proposed_by", referencedColumnName = "id")
    private AppUser proposedBy;

    // Nowe relacje
    @ManyToMany
    @JoinTable(
            name = "proposed_topic_category",
            joinColumns = @JoinColumn(name = "proposed_topic_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;

    @ManyToMany
    @JoinTable(
            name = "proposed_topic_tag",
            joinColumns = @JoinColumn(name = "proposed_topic_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags;
}