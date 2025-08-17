package io.mhetko.lor.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "vote_count")
@Data
public class VoteCount {

    @Id
    private Long id;
    private int leftCount;
    private int rightCount;
}