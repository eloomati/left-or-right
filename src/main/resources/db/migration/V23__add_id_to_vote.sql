ALTER TABLE vote ADD COLUMN proposed_topic_id BIGINT;
ALTER TABLE vote ADD CONSTRAINT fk_vote_proposed_topic FOREIGN KEY (proposed_topic_id) REFERENCES proposed_topic(id);