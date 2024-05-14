package com.yupao.manager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedisLimiterManagerTest {

    //正常最多1秒两次
    @Resource
    private RedisLimiterManager redisLimiterManager;
    @Test
    void doRateLimit() throws InterruptedException {
        String key = "1";
        for (int i = 0; i < 2; i++) {
            redisLimiterManager.doRateLimit(key);
            System.out.println("成功");
        }
        Thread.sleep(1000);
        //瞬间执行5次，成功一次打印成功
        for (int i = 0; i < 5; i++) {
            redisLimiterManager.doRateLimit(key);
            System.out.println("成功");
        }
    }
}