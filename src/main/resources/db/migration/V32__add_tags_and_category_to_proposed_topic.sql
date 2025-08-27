-- Dodanie tabeli pośredniej dla kategorii
CREATE TABLE proposed_topic_category (
                                         proposed_topic_id INT NOT NULL,
                                         category_id INT NOT NULL,
                                         PRIMARY KEY (proposed_topic_id, category_id),
                                         FOREIGN KEY (proposed_topic_id) REFERENCES proposed_topic(id) ON DELETE CASCADE,
                                         FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE CASCADE
);

-- Dodanie tabeli pośredniej dla tagów
CREATE TABLE proposed_topic_tag (
                                    proposed_topic_id INT NOT NULL,
                                    tag_id INT NOT NULL,
                                    PRIMARY KEY (proposed_topic_id, tag_id),
                                    FOREIGN KEY (proposed_topic_id) REFERENCES proposed_topic(id) ON DELETE CASCADE,
                                    FOREIGN KEY (tag_id) REFERENCES tag(id) ON DELETE CASCADE
);

CREATE INDEX idx_proposed_topic_category ON proposed_topic_category(category_id);
CREATE INDEX idx_proposed_topic_tag ON proposed_topic_tag(tag_id);