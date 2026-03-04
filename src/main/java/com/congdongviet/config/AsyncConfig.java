package com.congdongviet.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Slf4j
@EnableAsync
@Configuration
public class AsyncConfig implements AsyncConfigurer {

    /**
     * Dedicated thread pool for outbound SMS via Twilio.
     *
     * Sizing rationale for 100k+ users:
     *  - SMS calls are I/O-bound (Twilio HTTP round-trip ~200–500 ms)
     *  - 20 core threads handle ~40–100 req/s sustained
     *  - Queue of 500 absorbs burst spikes without rejecting requests
     *  - Rejected tasks fall back to the caller thread (CallerRunsPolicy)
     *    so no OTP is silently dropped
     */
    @Bean(name = "smsExecutor")
    public Executor smsExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("sms-");
        executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    /**
     * Catches unhandled exceptions from @Async void methods (safety net).
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) ->
                log.error("Unhandled async exception in {}: {}", method.getName(), ex.getMessage(), ex);
    }
}
