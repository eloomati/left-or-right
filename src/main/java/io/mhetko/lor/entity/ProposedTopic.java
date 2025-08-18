package io.mhetko.lor.entity;

import io.mhetko.lor.entity.enums.ProposedTopicSource;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;
}
