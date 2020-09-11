package com.example.demo.rocketmq;

import org.springframework.stereotype.Service;

import java.lang.annotation.*;

/**
 * @author lbing
 * @date 2020/09/11 10:25
 * @describe xxx
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Service
public @interface MQConsumeService {
    /**
     * 消息主题
     */
    TopicEnum topic();

    /**
     * 消息标签,如果是该主题下所有的标签，使用“*”
     */
    String[] tags();


}