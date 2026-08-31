package com.soulin.api.safety.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ContentSafetyConfig {
    @Bean("crisisDetectionRestTemplate")
    public RestTemplate crisisDetectionRestTemplate(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${app.crisis-detection.connect-timeout-ms:2000}") long connectTimeoutMs,
            @Value("${app.crisis-detection.read-timeout-ms:5000}") long readTimeoutMs
    ) {
        return restTemplateBuilder
                .connectTimeout(Duration.ofMillis(connectTimeoutMs))
                .readTimeout(Duration.ofMillis(readTimeoutMs))
                .build();
    }

    @Bean("contentSafetyExecutor")
    public Executor contentSafetyExecutor(
            @Value("${app.content-safety.pool-size:4}") int poolSize
    ) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(poolSize);
        executor.setMaxPoolSize(poolSize);
        executor.setQueueCapacity(poolSize * 10);
        executor.setThreadNamePrefix("content-safety-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
