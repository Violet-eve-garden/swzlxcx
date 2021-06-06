package com.sysu.swzl.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池设置
 * @author 49367
 * @date 2021/6/1 22:12
 */
@Configuration
@EnableConfigurationProperties(ThreadPoolProperties.class)
public class SwzlThreadPoolConfig {

    @Bean
    public ThreadPoolExecutor getMyThreadPool(ThreadPoolProperties threadPoolProperties){
        return new ThreadPoolExecutor(threadPoolProperties.getCorePoolSize(), threadPoolProperties.getMaximumPoolSize(),
                threadPoolProperties.getKeepAliveTime(), TimeUnit.SECONDS, new LinkedBlockingQueue<>(threadPoolProperties.getQueueSize()));
    }
}
