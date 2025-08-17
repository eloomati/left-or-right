ALTER TABLE vote_count DROP CONSTRAINT vote_count_pkey;
ALTER TABLE vote_count ADD COLUMN id BIGSERIAL PRIMARY KEY;
CREATE UNIQUE INDEX vote_count_topic_id_uindex ON vote_count (topic_id);