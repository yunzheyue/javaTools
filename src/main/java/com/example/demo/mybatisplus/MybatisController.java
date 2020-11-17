package com.example.demo.mybatisplus;

import com.example.demo.common.JsonResult;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Properties;
import java.util.concurrent.Future;

/**
 * @author: lbing
 * @description:
 * @date: Created in 16:49 2020/9/3
 */
@RequestMapping(value = "mybatis/plus")
@RestController
public class MybatisController {


    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public Object query() throws IOException {


        return JsonResult.jsonResult("发送结果==");
    }



}
