package io.mhetko.lor.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.List;
import io.mhetko.lor.entity.enums.TopicStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "topic")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TopicStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "popularity_score")
    private Integer popularityScore;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "is_archive")
    private Boolean isArchive;

    // ZAMIANA: wiele kategorii
    @ManyToMany
    @JoinTable(
            name = "topic_category",
            joinColumns = @JoinColumn(name = "topic_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Category> categories;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;

    @ManyToOne
    @JoinColumn(name = "continent_id")
    private Continent continent;

    @ManyToOne
    @JoinColumn(name = "created_by", referencedColumnName = "id")
    private AppUser createdBy;

    @ManyToMany
    @JoinTable(
            name = "topic_tag",
            joinColumns = @JoinColumn(name = "topic_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Tag> tags;
}