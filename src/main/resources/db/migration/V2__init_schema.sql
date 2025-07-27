-- =======================
-- TABELA: CATEGORY
-- =======================
CREATE TABLE category (
                          id SERIAL PRIMARY KEY,
                          name VARCHAR(100) UNIQUE NOT NULL
);

-- =======================
-- TABELA: USERS
-- =======================
CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       email VARCHAR(100) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(20) NOT NULL DEFAULT 'USER',
                       avatar_url TEXT,
                       country VARCHAR(100),
                       continent VARCHAR(100)
);

-- =======================
-- TABELA: USER_CATEGORIES
-- =======================
CREATE TABLE user_categories (
                                 user_id INT NOT NULL,
                                 category_id INT NOT NULL,
                                 PRIMARY KEY (user_id, category_id),
                                 FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                 FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE CASCADE
);

-- =======================
-- TABELA: TOPIC
-- =======================
CREATE TABLE topic (
                       id SERIAL PRIMARY KEY,
                       title TEXT NOT NULL,
                       description TEXT,
                       status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       popularity_score INT NOT NULL DEFAULT 0,
                       created_by INT NOT NULL,
                       category_id INT NOT NULL,
                       country VARCHAR(100),
                       continent VARCHAR(100),
                       FOREIGN KEY (created_by) REFERENCES users(id),
                       FOREIGN KEY (category_id) REFERENCES category(id)
);

-- =======================
-- TABELA: VOTE
-- =======================
CREATE TABLE vote (
                      id SERIAL PRIMARY KEY,
                      user_id INT NOT NULL,
                      topic_id INT NOT NULL,
                      side VARCHAR(10) NOT NULL CHECK (side IN ('LEFT', 'RIGHT')),
                      UNIQUE (user_id, topic_id),
                      FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                      FOREIGN KEY (topic_id) REFERENCES topic(id) ON DELETE CASCADE
);

-- =======================
-- TABELA: COMMENT
-- =======================
CREATE TABLE comment (
                         id SERIAL PRIMARY KEY,
                         user_id INT NOT NULL,
                         topic_id INT NOT NULL,
                         side VARCHAR(10) NOT NULL CHECK (side IN ('LEFT', 'RIGHT')),
                         content TEXT NOT NULL,
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                         FOREIGN KEY (topic_id) REFERENCES topic(id) ON DELETE CASCADE
);

-- =======================
-- TABELA: FOLLOWED_TOPIC
-- =======================
CREATE TABLE followed_topic (
                                id SERIAL PRIMARY KEY,
                                user_id INT NOT NULL,
                                topic_id INT NOT NULL,
                                UNIQUE (user_id, topic_id),
                                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                FOREIGN KEY (topic_id) REFERENCES topic(id) ON DELETE CASCADE
);

-- =======================
-- TABELA: PROPOSED_TOPIC
-- =======================
CREATE TABLE proposed_topic (
                                id SERIAL PRIMARY KEY,
                                title TEXT NOT NULL,
                                description TEXT,
                                source VARCHAR(20) NOT NULL CHECK (source IN ('USER', 'API')),
                                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                proposed_by INT,
                                category_id INT NOT NULL,
                                FOREIGN KEY (proposed_by) REFERENCES users(id),
                                FOREIGN KEY (category_id) REFERENCES category(id)
);

-- =======================
-- TABELA: BANNED_USER
-- =======================
CREATE TABLE banned_user (
                             id SERIAL PRIMARY KEY,
                             user_id INT NOT NULL UNIQUE,
                             reason TEXT,
                             banned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             banned_by INT,
                             FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                             FOREIGN KEY (banned_by) REFERENCES users(id)
);

-- =======================
-- TABELA: NOTIFICATION
-- =======================
CREATE TABLE notification (
                              id SERIAL PRIMARY KEY,
                              user_id INT NOT NULL,
                              message TEXT NOT NULL,
                              is_read BOOLEAN DEFAULT FALSE,
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- =======================
-- TABELA: TAG
-- =======================
CREATE TABLE tag (
                     id SERIAL PRIMARY KEY,
                     name VARCHAR(50) UNIQUE NOT NULL
);

-- =======================
-- TABELA: TOPIC_TAGS
-- =======================
CREATE TABLE topic_tags (
                            topic_id INT NOT NULL,
                            tag_id INT NOT NULL,
                            PRIMARY KEY (topic_id, tag_id),
                            FOREIGN KEY (topic_id) REFERENCES topic(id) ON DELETE CASCADE,
                            FOREIGN KEY (tag_id) REFERENCES tag(id) ON DELETE CASCADE
);

-- =======================
-- TABELA: REPORT
-- =======================
CREATE TABLE report (
                        id SERIAL PRIMARY KEY,
                        reporter_id INT NOT NULL,
                        comment_id INT,
                        topic_id INT,
                        reason TEXT NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (reporter_id) REFERENCES users(id) ON DELETE CASCADE,
                        FOREIGN KEY (comment_id) REFERENCES comment(id) ON DELETE SET NULL,
                        FOREIGN KEY (topic_id) REFERENCES topic(id) ON DELETE SET NULL
);
