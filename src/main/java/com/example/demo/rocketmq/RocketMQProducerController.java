package com.example.demo.rocketmq;

import com.example.demo.common.JsonResult;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author: lbing
 * @description:
 * @date: Created in 16:49 2020/9/3
 */
@RequestMapping(value = "rocketmq")
@RestController
public class RocketMQProducerController {

    @Autowired
    private DefaultMQProducer defaultMQProducer;

    @RequestMapping(value = "/producer", method = RequestMethod.GET)
    public Object consume(HttpServletRequest request, HttpServletResponse response) throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
        String msg = "测试数据";
        Message sendMsg = new Message("TopicTest","DemoTag","mesh",msg.getBytes());
        //默认3秒超时
        SendResult sendResult = defaultMQProducer.send(sendMsg);

        return JsonResult.jsonResult("发送结果=="+sendResult);
    }

}
