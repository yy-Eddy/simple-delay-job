package com.github.yyeddy.sdj.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@EnableAsync
@Configuration
public class AsyncConfig implements AsyncConfigurer {

    private Executor executor;

    @Override
    public Executor getAsyncExecutor() {
        return this.executor;
    }

    @Bean
    @ConditionalOnMissingBean(ThreadPoolTaskExecutor.class)
    public ThreadPoolTaskExecutor threadPoolTaskExecutor(){
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        int threadNum = Runtime.getRuntime().availableProcessors();
        threadPoolTaskExecutor.setCorePoolSize(threadNum);
        threadPoolTaskExecutor.setAllowCoreThreadTimeOut(true);
        threadPoolTaskExecutor.setMaxPoolSize(threadNum * 2 + 2);
        threadPoolTaskExecutor.setKeepAliveSeconds(120);
        threadPoolTaskExecutor.setQueueCapacity(100);
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        threadPoolTaskExecutor.setThreadNamePrefix("threadPoolTaskExecutor-service-");
        threadPoolTaskExecutor.initialize();
        executor = threadPoolTaskExecutor;
        return threadPoolTaskExecutor;
    }
}
