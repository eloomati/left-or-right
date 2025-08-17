-- indeksy do głosów
CREATE INDEX idx_vote_user_topic ON vote(user_id, topic_id) WHERE is_deleted = false;
CREATE INDEX idx_vote_topic_side ON vote(topic_id, side) WHERE is_deleted = false;

-- indeks na vote_count
CREATE UNIQUE INDEX idx_vote_count_topic ON vote_count(topic_id);

-- partial unique constraint na unikalny głos (user, topic)
CREATE UNIQUE INDEX uq_vote_user_topic_unique
    ON vote(user_id, topic_id)
    WHERE is_deleted = false;

-- trigger do automatycznej aktualizacji popularności (jeśli nie chcesz robić tego w aplikacji)
CREATE OR REPLACE FUNCTION update_popularity_on_vote()
    RETURNS TRIGGER AS $$
BEGIN
    UPDATE topic
    SET popularity_score = (
        SELECT COALESCE(SUM(CASE WHEN side = 'LEFT' THEN 1 ELSE 0 END), 0) +
               COALESCE(SUM(CASE WHEN side = 'RIGHT' THEN 1 ELSE 0 END), 0)
        FROM vote
        WHERE topic_id = NEW.topic_id AND is_deleted = false
    )
    WHERE id = NEW.topic_id;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_vote_popularity
    AFTER INSERT OR UPDATE OR DELETE ON vote
    FOR EACH ROW
EXECUTE FUNCTION update_popularity_on_vote();
