// io/mhetko/lor/entity/Vote.java
package io.mhetko.lor.entity;

import io.mhetko.lor.entity.enums.Side;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class Vote {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private AppUser user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Topic topic;

    @ManyToOne(fetch = FetchType.LAZY)
    private ProposedTopic proposedTopic;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Side side;

    @Column(nullable = false)
    private Boolean isDeleted = false;

    private LocalDateTime deletedAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (updatedAt == null) updatedAt = LocalDateTime.now();
        if (isDeleted == null) isDeleted = false;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Fabryki (minimalizują powtarzanie defaultów)
    public static Vote forTopic(AppUser user, Topic topic, Side side) {
        Vote v = new Vote();
        v.setUser(user);
        v.setTopic(topic);
        v.setSide(side);
        v.setIsDeleted(false);
        v.setDeletedAt(null);
        v.setUpdatedAt(LocalDateTime.now());
        return v;
    }

    public static Vote forProposed(AppUser user, ProposedTopic proposedTopic, Side side) {
        Vote v = new Vote();
        v.setUser(user);
        v.setProposedTopic(proposedTopic);
        v.setSide(side);
        v.setIsDeleted(false);
        v.setDeletedAt(null);
        v.setUpdatedAt(LocalDateTime.now());
        return v;
    }
}
