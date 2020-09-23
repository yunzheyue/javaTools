package com.example.demo.redislock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author lbing
 * @description xxx
 * @date Created in 15:16 2020/09/23
 */
public class RedisLock {

    private RedisTemplate redisTemplate;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * simple lock尝试获取锅的次数
     */
    private int retryCount = 3;

    /**
     * 每次尝试获取锁的重试间隔毫秒数
     */
    private int waitIntervalInMS = 100;


    public RedisLock(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 利用redis获取分布式锁(未获取锁的请求，允许丢弃!)
     *
     * @param redisKey       锁的key值
     * @param expireInSecond 锁的自动释放时间(秒)
     * @return
     * @throws Exception
     */
    public String simpleLock(final String redisKey, final int expireInSecond) throws Exception {
        String lockValue = UUID.randomUUID().toString();
        boolean flag = false;
        if (StringUtils.isEmpty(redisKey)) {
            throw new Exception("key is empty!");
        }
        if (expireInSecond <= 0) {
            throw new Exception("expireInSecond must be bigger than 0");
        }
        try {
            for (int i = 0; i < retryCount; i++) {
                boolean success = redisTemplate.opsForValue().setIfAbsent(redisKey, lockValue, expireInSecond, TimeUnit.SECONDS);
                if (success) {
                    flag = true;
                    break;
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(waitIntervalInMS);
                } catch (Exception ignore) {
                    logger.warn("redis lock fail: " + ignore.getMessage());

                }
            }
            if (!flag) {
                throw new Exception(Thread.currentThread().getName() + " cannot acquire lock now ...");
            }
            return lockValue;
        } catch (Exception e) {
            logger.warn("get redis lock error, exception: " + e.getMessage());
            throw e;
        }
    }

    /**
     * 利用redis获取分布式锁(未获取锁的请求，将在timeoutSecond时间范围内，一直等待重试)
     *
     * @param redisKey       锁的key值
     * @param expireInSecond 锁的自动释放时间(秒)
     * @param timeoutSecond  未获取到锁的请求，尝试重试的最久等待时间(秒)
     * @return
     * @throws Exception
     */
    public String lock(final String redisKey, final int expireInSecond, final int timeoutSecond) throws Exception {
        String lockValue = UUID.randomUUID().toString();
        boolean flag = false;
        if (StringUtils.isEmpty(redisKey)) {
            throw new Exception("key is empty!");
        }
        if (expireInSecond <= 0) {
            throw new Exception("expireInSecond must be greater than 0");
        }
        if (timeoutSecond <= 0) {
            throw new Exception("timeoutSecond must be greater than 0");
        }
        if (timeoutSecond >= expireInSecond) {
            throw new Exception("timeoutSecond must be less than expireInSecond");
        }
        try {
            long timeoutAt = System.currentTimeMillis() + timeoutSecond * 1000;
            while (true) {
                boolean success = redisTemplate.opsForValue().setIfAbsent(redisKey, lockValue, expireInSecond, TimeUnit.SECONDS);
                if (success) {
                    flag = true;
                    break;
                }
                if (System.currentTimeMillis() >= timeoutAt) {
                    break;
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(waitIntervalInMS);
                } catch (Exception ignore) {
                    logger.warn("redis lock fail: " + ignore.getMessage());
                }
            }
            if (!flag) {
                throw new Exception(Thread.currentThread().getName() + " cannot acquire lock now ...");
            }
            return lockValue;
        } catch (Exception e) {
            logger.warn("get redis lock error, exception: " + e.getMessage());
            throw e;
        }
    }


    /**
     * 锁释放
     *
     * @param redisKey
     * @param lockValue
     */
    public void unlock(final String redisKey, final String lockValue) {
        if (StringUtils.isEmpty(redisKey)) {
            return;
        }
        if (StringUtils.isEmpty(lockValue)) {
            return;
        }
        try {
            String currLockVal = (String) redisTemplate.opsForValue().get(redisKey);
            if (currLockVal != null && currLockVal.equals(lockValue)) {
                boolean result = redisTemplate.delete(redisKey);
                if (!result) {
                    logger.warn(Thread.currentThread().getName() + " unlock redis lock fail");
                } else {
                    logger.info(Thread.currentThread().getName() + " unlock redis lock:" + redisKey + " successfully!");
                }
            }
        } catch (Exception je) {
            logger.warn(Thread.currentThread().getName() + " unlock redis lock error:" + je.getMessage());
        }
    }
}
