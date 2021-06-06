package com.sysu.swzl.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 线程池参数配置
 * @author 49367
 * @date 2021/6/1 22:11
 */
@Data
@ConfigurationProperties(prefix = "swzl.thread")
public class ThreadPoolProperties {
    private int corePoolSize;
    private int maximumPoolSize;
    private long keepAliveTime;
    private int queueSize;
}
