CREATE TABLE topic_watch (
                             id BIGSERIAL PRIMARY KEY,
                             user_id BIGINT NOT NULL,
                             topic_id BIGINT NOT NULL,
                             created_at TIMESTAMP NOT NULL DEFAULT now(),
                             CONSTRAINT fk_topic_watch_user FOREIGN KEY (user_id) REFERENCES app_user(id),
                             CONSTRAINT fk_topic_watch_topic FOREIGN KEY (topic_id) REFERENCES topic(id),
                             CONSTRAINT uq_topic_watch_user_topic UNIQUE (user_id, topic_id)
);

CREATE INDEX idx_topic_watch_user_id ON topic_watch(user_id);
CREATE INDEX idx_topic_watch_topic_id ON topic_watch(topic_id);