CREATE TABLE vote_count (
                            topic_id INT PRIMARY KEY,
                            left_count INT NOT NULL DEFAULT 0,
                            right_count INT NOT NULL DEFAULT 0,
                            FOREIGN KEY (topic_id) REFERENCES topic(id) ON DELETE CASCADE
);