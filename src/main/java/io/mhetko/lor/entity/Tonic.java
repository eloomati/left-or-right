package io.mhetko.lor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tonic")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tonic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Size(min = 5, max = 255)
    private String title;
    @Size(min = 50, max = 1000)
    private String description;
    @Size(max = 20)
    private String status;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Column(name = "popularity_score")
    private Integer popularityScore;
    @Column(name = "created_by")
    private Long createdBy;
    @Column(name = "category_id")
    private Long categoryId;
    @Size(max = 100)
    private String country;
    @Size(max = 100)
    private String continent;
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    @Column(name = "is_archive")
    private Boolean isArchive;
}
