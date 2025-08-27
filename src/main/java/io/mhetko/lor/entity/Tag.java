package io.mhetko.lor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "tag")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToMany(mappedBy = "tags")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Topic> topics;
}
