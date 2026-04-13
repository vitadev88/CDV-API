-- V34: Add scheduled publishing support to blog_posts
--
-- Adds the scheduled_at column used by BlogScheduler to auto-publish posts.
-- A post with status='SCHEDULED' and scheduled_at <= NOW() will be published
-- automatically by the scheduler job (runs every 60 seconds).

ALTER TABLE blog_posts
    ADD COLUMN scheduled_at DATETIME NULL DEFAULT NULL
        COMMENT 'Target publish time for SCHEDULED posts. Cleared to NULL on publish.';

-- Index to make the scheduler query fast (only scans SCHEDULED rows by time)
CREATE INDEX idx_blog_posts_scheduled
    ON blog_posts (status, scheduled_at);
