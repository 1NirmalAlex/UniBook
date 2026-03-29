-- Run after unibook_schema.sql
USE unibook;

-- Optional migration if you created the DB from an older unibook_schema.sql without this table.
CREATE TABLE IF NOT EXISTS user_achievements (
    achievement_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id BIGINT UNSIGNED NOT NULL,
    title VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (achievement_id),
    KEY idx_user_achievements_user (user_id),
    CONSTRAINT fk_user_achievements_user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
