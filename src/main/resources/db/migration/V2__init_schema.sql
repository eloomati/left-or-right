-- =======================
-- TABELA: CATEGORY
-- =======================
CREATE TABLE category (
                          id SERIAL PRIMARY KEY,
                          name VARCHAR(100) UNIQUE NOT NULL,
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          deleted_at TIMESTAMP NULL
);

-- =======================
-- TABELA: USER_APP
-- =======================
CREATE TABLE app_user (
                          id SERIAL PRIMARY KEY,
                          username VARCHAR(50) UNIQUE NOT NULL,
                          email VARCHAR(100) UNIQUE NOT NULL,
                          password VARCHAR(255) NOT NULL,
                          role VARCHAR(20) NOT NULL DEFAULT 'USER',
                          avatar_url TEXT,
                          country VARCHAR(100),
                          continent VARCHAR(100),
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          last_login_at TIMESTAMP NULL,
                          deleted_at TIMESTAMP NULL,
                          is_active BOOLEAN NOT NULL DEFAULT TRUE
);

-- =======================
-- TABELA: USER_CATEGORY
-- =======================
CREATE TABLE user_category (
                               user_id INT NOT NULL,
                               category_id INT NOT NULL,
                               PRIMARY KEY (user_id, category_id),
                               FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE,
                               FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE CASCADE
);

CREATE INDEX idx_user_category_user_id ON user_category(user_id);
CREATE INDEX idx_user_category_category_id ON user_category(category_id);
-- =======================
-- TABELA: TOPIC
-- =======================
CREATE TABLE topic (
                       id SERIAL PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       description VARCHAR(1000),
                       status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       popularity_score INT NOT NULL DEFAULT 0,
                       created_by INT NOT NULL,
                       category_id INT NOT NULL,
                       country VARCHAR(100),
                       continent VARCHAR(100),
                       deleted_at TIMESTAMP NULL,
                       is_archive BOOLEAN NOT NULL DEFAULT FALSE,
                       FOREIGN KEY (created_by) REFERENCES app_user(id),
                       FOREIGN KEY (category_id) REFERENCES category(id)
);

CREATE INDEX idx_topic_created_by ON topic(created_by);
CREATE INDEX idx_topic_category_id ON topic(category_id);

-- =======================
-- TABELA: VOTE
-- =======================
CREATE TABLE vote (
                      id SERIAL PRIMARY KEY,
                      user_id INT NOT NULL,
                      topic_id INT NOT NULL,
                      side VARCHAR(10) NOT NULL CHECK (side IN ('LEFT', 'RIGHT')),
                      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      deleted_at TIMESTAMP NULL,
                      is_deleted BOOLEAN NOT NULL DEFAULT FALSE,

                      UNIQUE (user_id, topic_id),
                      FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE,
                      FOREIGN KEY (topic_id) REFERENCES topic(id) ON DELETE CASCADE
);

CREATE INDEX idx_vote_user_id ON vote(user_id);
CREATE INDEX idx_vote_topic_id ON vote(topic_id);

-- =======================
-- TABELA: COMMENT
-- =======================
CREATE TABLE comment (
                         id SERIAL PRIMARY KEY,
                         user_id INT NOT NULL,
                         topic_id INT NOT NULL,
                         side VARCHAR(10) NOT NULL CHECK (side IN ('LEFT', 'RIGHT')),
                         content VARCHAR(2000) NOT NULL,
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         deleted_at TIMESTAMP NULL,
                         FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE,
                         FOREIGN KEY (topic_id) REFERENCES topic(id) ON DELETE CASCADE
);

CREATE INDEX idx_comment_user_id ON comment(user_id);
CREATE INDEX idx_comment_topic_id ON comment(topic_id);

-- =======================
-- TABELA: FOLLOWED_TOPIC
-- =======================
CREATE TABLE followed_topic (
                                user_id INT NOT NULL,
                                topic_id INT NOT NULL,
                                PRIMARY KEY (user_id, topic_id),
                                FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE,
                                FOREIGN KEY (topic_id) REFERENCES topic(id) ON DELETE CASCADE
);

CREATE INDEX idx_followed_topic_user_id ON followed_topic(user_id);
CREATE INDEX idx_followed_topic_topic_id ON followed_topic(topic_id);
-- =======================
-- TABELA: PROPOSED_TOPIC
-- =======================
CREATE TABLE proposed_topic (
                                id SERIAL PRIMARY KEY,
                                title VARCHAR(255) NOT NULL,
                                description VARCHAR(1000),
                                source VARCHAR(20) NOT NULL CHECK (source IN ('USER', 'API')),
                                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                proposed_by INT,
                                category_id INT NOT NULL,
                                deleted_at TIMESTAMP NULL,
                                FOREIGN KEY (proposed_by) REFERENCES app_user(id),
                                FOREIGN KEY (category_id) REFERENCES category(id)
);

CREATE INDEX idx_proposed_topic_proposed_by ON proposed_topic(proposed_by);
CREATE INDEX idx_proposed_topic_category_id ON proposed_topic(category_id);

-- =======================
-- TABELA: BANNED_USER
-- =======================
CREATE TABLE banned_user (
                             user_id INT PRIMARY KEY,
                             reason VARCHAR(500),
                             banned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             banned_by INT,
                             created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             deleted_at TIMESTAMP NULL,
                             FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE,
                             FOREIGN KEY (banned_by) REFERENCES app_user(id)
);
CREATE INDEX idx_banned_user_banned_by ON banned_user(banned_by);

-- =======================
-- TABELA: NOTIFICATION
-- =======================
CREATE TABLE notification (
                              id SERIAL PRIMARY KEY,
                              user_id INT NOT NULL,
                              message VARCHAR(1000) NOT NULL,
                              is_read BOOLEAN DEFAULT FALSE,
                              created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              deleted_at TIMESTAMP NULL,
                              FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE
);

CREATE INDEX idx_notification_user_id ON notification(user_id);

-- =======================
-- TABELA: TAG
-- =======================

CREATE TABLE tag (
                     id SERIAL PRIMARY KEY,
                     name VARCHAR(50) UNIQUE NOT NULL,
                     created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                     deleted_at TIMESTAMP NULL
);

-- =======================
-- TABELA: TOPIC_TAG
-- =======================
CREATE TABLE topic_tag (
                           topic_id INT NOT NULL,
                           tag_id INT NOT NULL,
                           PRIMARY KEY (topic_id, tag_id),
                           FOREIGN KEY (topic_id) REFERENCES topic(id) ON DELETE CASCADE,
                           FOREIGN KEY (tag_id) REFERENCES tag(id) ON DELETE CASCADE
);

CREATE INDEX idx_topic_tag_topic_id ON topic_tag(topic_id);
CREATE INDEX idx_topic_tag_tag_id ON topic_tag(tag_id);
-- =======================
-- TABELA: REPORT
-- =======================
CREATE TABLE report (
                        id SERIAL PRIMARY KEY,
                        reporter_id INT NOT NULL,
                        comment_id INT,
                        topic_id INT,
                        reason VARCHAR(1000) NOT NULL,
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        deleted_at TIMESTAMP NULL,
                        FOREIGN KEY (reporter_id) REFERENCES app_user(id) ON DELETE CASCADE,
                        FOREIGN KEY (comment_id) REFERENCES comment(id) ON DELETE SET NULL,
                        FOREIGN KEY (topic_id) REFERENCES topic(id) ON DELETE SET NULL
);

CREATE INDEX idx_report_reporter_id ON report(reporter_id);
CREATE INDEX idx_report_comment_id ON report(comment_id);
CREATE INDEX idx_report_topic_id ON report(topic_id);