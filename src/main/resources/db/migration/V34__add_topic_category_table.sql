-- src/main/resources/db/migration/V34__add_topic_category_table.sql
CREATE TABLE topic_category (
                                topic_id INT NOT NULL,
                                category_id INT NOT NULL,
                                PRIMARY KEY (topic_id, category_id),
                                FOREIGN KEY (topic_id) REFERENCES topic(id) ON DELETE CASCADE,
                                FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE CASCADE
);

CREATE INDEX idx_topic_category ON topic_category(category_id);

-- (opcjonalnie) usuń kolumnę category_id z topic jeśli istnieje:
ALTER TABLE topic DROP COLUMN IF EXISTS category_id;