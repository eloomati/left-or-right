package io.mhetko.lor.entity;

import io.mhetko.lor.entity.enums.Side;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Data;

@Entity
@Table(name = "vote_count")
@Data
public class VoteCount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "topic_id", nullable = false, unique = true)
    private Topic topic;

    private int leftCount = 0;
    private int rightCount = 0;

    @Version
    private Long version;

    public static VoteCount forTopic(Topic topic) {
        VoteCount vc = new VoteCount();
        vc.setTopic(topic);
        vc.setLeftCount(0);
        vc.setRightCount(0);
        return vc;
    }

    public static VoteCount forProposed(ProposedTopic proposedTopic) {
        VoteCount vc = new VoteCount();
        vc.setProposedTopic(proposedTopic);
        vc.setLeftCount(0);
        vc.setRightCount(0);
        return vc;
    }

    public void increment(Side side) {
        if (side == Side.LEFT) leftCount++; else rightCount++;
    }

    public void decrement(Side side) {
        if (side == Side.LEFT) leftCount--; else rightCount--;
        if (leftCount < 0) leftCount = 0;
        if (rightCount < 0) rightCount = 0;
    }

    public int getTotal() {
        return leftCount + rightCount;
    }

    @ManyToOne
    @JoinColumn(name = "proposed_topic_id")
    private ProposedTopic proposedTopic;
}