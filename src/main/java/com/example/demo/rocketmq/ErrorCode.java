package com.example.demo.rocketmq;

import java.io.Serializable;

/**
 * @author: lbing
 * @description:
 * @date: Created in 15:03 2020/9/10
 */

public interface ErrorCode extends Serializable {
    /**
     * 错误码
     * @return
     */
    String getCode();
    /**
     * 错误信息
     * @return
     */
    String getMsg();
}