CREATE TABLE audit_logs (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    action     VARCHAR(255) NOT NULL,
    actor      VARCHAR(100) NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_audit_logs_actor  ON audit_logs (actor);
CREATE INDEX idx_audit_logs_action ON audit_logs (action);
