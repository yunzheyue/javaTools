package com.example.demo.redislock;

import com.example.demo.common.JsonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author lbing
 * @description xxx
 * @date Created in 15:22 2020/09/23
 */
@RequestMapping(value = "redis")
@RestController
public class RedisLockController {

    @Resource(name = "redisOneTemplate")
    private RedisTemplate redisTemplate;

    private static Logger logger = LoggerFactory.getLogger(RedisLockController.class);

    @RequestMapping(value = "/lock", method = RequestMethod.GET)
    public Object lock(HttpServletRequest request, HttpServletResponse response) throws Exception {

        fun();

        return JsonResult.jsonResult("发送结果==");
    }


    public void fun() throws InterruptedException {

        RedisLock redisLock = new RedisLock(redisTemplate);
        String lockKey = "lock:test";
        /**
         * CountDownLatch:同步计数器，参数为计数器的初始值，没调用一次countDown()，计数器减1，
         *                当计数器大于0时，await()方法会阻塞程序继续执行，等于0时不会阻塞。
         */
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch threadsLatch = new CountDownLatch(2);

        final int lockExpireSecond = 5;
        final int timeoutSecond = 3;

        Runnable lockRunnable = () -> {
            String lockValue = "";
            try {
                //等待发令枪响，防止线程抢跑
                start.await();

                //允许丢数据的简单锁示例
//                lockValue = redisLock.simpleLock(lockKey, lockExpireSecond);


                //不允许丢数据的分布式锁示例
                lockValue = redisLock.lock(lockKey, lockExpireSecond, timeoutSecond);

                //停一会儿，故意让后面的线程抢不到锁
                TimeUnit.SECONDS.sleep(2);
                logger.info(String.format("%s get lock successfully, value:%s", Thread.currentThread().getName(), lockValue));

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                redisLock.unlock(lockKey, lockValue);
                //执行完后，计数减1
                threadsLatch.countDown();
            }

        };

        Thread t1 = new Thread(lockRunnable, "T1");
        Thread t2 = new Thread(lockRunnable, "T2");

        t1.start();
        t2.start();

        //预备：开始！
        start.countDown();

        //等待所有线程跑完
        threadsLatch.await();

        logger.info("======>done!!!");

    }

}
