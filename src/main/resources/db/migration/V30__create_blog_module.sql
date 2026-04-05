-- Blog Categories
CREATE TABLE blog_categories (
    id          CHAR(36)     NOT NULL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    slug        VARCHAR(120) NOT NULL UNIQUE,
    description TEXT,
    created_at  DATETIME(6)  NOT NULL,
    updated_at  DATETIME(6)
);

-- Blog Posts
CREATE TABLE blog_posts (
    id               CHAR(36)     NOT NULL PRIMARY KEY,
    title            VARCHAR(255) NOT NULL,
    slug             VARCHAR(300) NOT NULL UNIQUE,
    content          LONGTEXT     NOT NULL,
    excerpt          VARCHAR(500),
    thumbnail        VARCHAR(500),
    category_id      CHAR(36),
    author_id        CHAR(36)     NOT NULL,
    tags             JSON,
    meta_title       VARCHAR(255),
    meta_description VARCHAR(320),
    status           VARCHAR(20)  NOT NULL DEFAULT 'DRAFT',
    view_count       BIGINT       NOT NULL DEFAULT 0,
    published_at     DATETIME(6),
    created_at       DATETIME(6)  NOT NULL,
    updated_at       DATETIME(6),
    CONSTRAINT fk_blog_post_category FOREIGN KEY (category_id) REFERENCES blog_categories (id) ON DELETE SET NULL,
    CONSTRAINT fk_blog_post_author   FOREIGN KEY (author_id)   REFERENCES users (id)
);

CREATE INDEX idx_blog_posts_slug       ON blog_posts (slug);
CREATE INDEX idx_blog_posts_status     ON blog_posts (status);
CREATE INDEX idx_blog_posts_category   ON blog_posts (category_id);
CREATE INDEX idx_blog_posts_author     ON blog_posts (author_id);
CREATE INDEX idx_blog_posts_published  ON blog_posts (published_at DESC);

-- Seed default category
INSERT INTO blog_categories (id, name, slug, description, created_at, updated_at)
VALUES (UUID(), 'Tin tức', 'tin-tuc', 'Tin tức cộng đồng', NOW(), NOW());
