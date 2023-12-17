package com.learnings.forum.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Created with IntelliJ IDEA.
 * Description: 线程池
 * User: 12569
 * Date: 2023-12-17
 * Time: 11:04
 */
@Configuration
public class ThreadPoolConfig {
    @Bean
    public ThreadPoolTaskExecutor taskExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(10000);
        executor.setThreadNamePrefix("MyThread-");
        executor.initialize();
        return executor;
    }

}
