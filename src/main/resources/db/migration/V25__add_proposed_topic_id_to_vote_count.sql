ALTER TABLE vote_count
    ADD COLUMN proposed_topic_id BIGINT;

ALTER TABLE vote_count
    ALTER COLUMN topic_id DROP NOT NULL;

ALTER TABLE vote_count
    ADD CONSTRAINT fk_vote_count_proposed_topic
        FOREIGN KEY (proposed_topic_id)
            REFERENCES proposed_topic(id);