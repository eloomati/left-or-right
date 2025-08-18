ALTER TABLE topic_watch ADD COLUMN proposed_topic_id bigint REFERENCES proposed_topic(id);
ALTER TABLE topic_watch ALTER COLUMN topic_id DROP NOT NULL;
CREATE INDEX idx_topic_watch_proposed_topic_id ON topic_watch(proposed_topic_id);