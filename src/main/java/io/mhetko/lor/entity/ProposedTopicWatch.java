package io.mhetko.lor.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "proposed_topic_watch")
@Data
@NoArgsConstructor
public class ProposedTopicWatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "proposed_topic_id", nullable = false)
    private ProposedTopic proposedTopic;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;
}