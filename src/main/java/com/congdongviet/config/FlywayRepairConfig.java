package com.congdongviet.config;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Gọi repair() trước migrate() để xóa các entry FAILED trong flyway_schema_history.
 * Cần thiết khi Flyway 10 community không hỗ trợ ignoreMigrationPatterns "*:failed".
 */
@Configuration
public class FlywayRepairConfig {

    @Bean
    public FlywayMigrationStrategy repairBeforeMigrate() {
        return flyway -> {
            flyway.repair();
            flyway.migrate();
        };
    }
}
