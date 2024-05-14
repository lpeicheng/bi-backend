package com.yupao.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

/**
 * 线程池配置
 */
@Configuration
public class ThreadPoolConfig {
    @Bean
    public ThreadPoolExecutor threadPoolTaskExecutor() {
        //创建线程工厂
        ThreadFactory threadFactory = new ThreadFactory() {
            //初始线程数为1
            int c=1;
            @Override
            //每当线程池要创建新线程时就调用这个方法
            // @NotNull Runnable r 表示方法参数 r 应该永远不为null，传递参数为null报错
            public Thread newThread(@NotNull Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("线程"+c);
                c++;
                return thread;
            }
        };

        /*
          创建线程池
         * 核心线程数 corePoolSize
         * 最大线程数 maximumPoolSize
         * 空闲线程存活时间 keepAliveTime
         * 存活时间单位 TimeUnit
         * 任务队列 workQueue
         * 线程工厂 threadFactory
         * 拒绝处理器 RejectedExecutionHandler
        */
        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 4,100 , TimeUnit.SECONDS, new ArrayBlockingQueue<>(20), threadFactory);
        return executor;
        }
    }
