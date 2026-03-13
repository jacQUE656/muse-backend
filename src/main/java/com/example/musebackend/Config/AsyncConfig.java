package com.example.musebackend.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2); // Minimum threads
        executor.setMaxPoolSize(5);  // Maximum threads
        executor.setQueueCapacity(100); // Wait list if all threads are busy
        executor.setThreadNamePrefix("EmailThread-");
        executor.initialize();
        return executor;
    }
}
