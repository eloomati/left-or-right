package io.mhetko.lor.entity;

import io.mhetko.lor.entity.enums.Side;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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

    public void increment(Side side) {
        if (side == Side.LEFT) {
            leftCount++;
        } else {
            rightCount++;
        }
    }

    public void decrement(Side side) {
        if (side == Side.LEFT) {
            leftCount = Math.max(0, leftCount - 1);
        } else {
            rightCount = Math.max(0, rightCount - 1);
        }
    }

    public int getTotal() {
        return leftCount + rightCount;
    }
}