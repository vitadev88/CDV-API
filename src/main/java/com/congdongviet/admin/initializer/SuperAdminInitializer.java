package com.congdongviet.admin.initializer;

import com.congdongviet.admin.service.SuperAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SuperAdminInitializer {

    private final SuperAdminService superAdminService;

    /**
     * Triggered after the application context is fully started and ready.
     * Using ApplicationReadyEvent (not ApplicationStartedEvent or @PostConstruct)
     * ensures Flyway migrations and all beans are fully initialized.
     *
     * The initializer is a thin orchestrator — all business logic lives in SuperAdminService.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("[BOOTSTRAP] Running SUPER_ADMIN bootstrap check...");
        superAdminService.bootstrapSuperAdmin();
    }
}
