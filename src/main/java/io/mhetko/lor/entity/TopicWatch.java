package io.mhetko.lor.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "topic_watch", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "topic_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicWatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    @Column(name = "created_at", nullable = false)
    private java.time.LocalDateTime createdAt;
}