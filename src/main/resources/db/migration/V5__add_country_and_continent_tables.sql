-- Tworzenie tabeli continent
CREATE TABLE continent (
                           id BIGSERIAL PRIMARY KEY,
                           name VARCHAR(100) NOT NULL UNIQUE
);

-- Tworzenie tabeli country
CREATE TABLE country (
                         id BIGSERIAL PRIMARY KEY,
                         name VARCHAR(100) NOT NULL UNIQUE,
                         code VARCHAR(10) NOT NULL UNIQUE,
                         continent_id BIGINT NOT NULL,
                         CONSTRAINT fk_country_continent FOREIGN KEY (continent_id) REFERENCES continent(id)
);

-- Dodanie kolumn do topic
ALTER TABLE topic
    ADD COLUMN country_id BIGINT,
    ADD COLUMN continent_id BIGINT;

ALTER TABLE topic
    ADD CONSTRAINT fk_topic_country FOREIGN KEY (country_id) REFERENCES country(id);

ALTER TABLE topic
    ADD CONSTRAINT fk_topic_continent FOREIGN KEY (continent_id) REFERENCES continent(id);

-- Usunięcie starych kolumn tekstowych (jeśli istnieją)
ALTER TABLE topic
    DROP COLUMN IF EXISTS country,
    DROP COLUMN IF EXISTS continent;