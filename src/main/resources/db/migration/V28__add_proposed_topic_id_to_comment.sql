ALTER TABLE comment
ADD COLUMN proposed_topic_id bigint REFERENCES proposed_topic(id);