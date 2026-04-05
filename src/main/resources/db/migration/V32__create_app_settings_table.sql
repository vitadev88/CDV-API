CREATE TABLE app_settings (
    id BIGINT PRIMARY KEY DEFAULT 1,
    show_featured BOOLEAN NOT NULL DEFAULT TRUE,
    show_featured_packages BOOLEAN NOT NULL DEFAULT TRUE,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO app_settings (id, show_featured, show_featured_packages)
VALUES (1, TRUE, TRUE)
ON DUPLICATE KEY UPDATE id = id;
