package com.example.demo.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author: lbing
 * @description:
 * @date: Created in 10:12 2020/9/4
 */
//@Component
public class RawDataListener1 extends RawDataListener {

    /**
     * 下面是分组进行监听
     */
//    @KafkaListener(groupId="test-consumer-group1",topics = "${kafka.consumer.topic}", containerFactory="kafkaListenerContainerFactory1")
    public void listen1(ConsumerRecord<?, ?> record) throws IOException {
        String value = (String) record.value();
        System.out.println("组1数据："+value);
    }
//    @KafkaListener(groupId="test-consumer-group2",topics = "${kafka.consumer.topic}",  containerFactory="kafkaListenerContainerFactory2")
    public void listen2(ConsumerRecord<?, ?> record) throws IOException {
        String value = (String) record.value();
        System.out.println("组2数据："+value);
    }

}
