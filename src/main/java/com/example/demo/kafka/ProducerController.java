package com.example.demo.kafka;

import com.example.demo.common.JsonResult;
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
import org.springframework.stereotype.Controller;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Properties;

/**
 * @author: lbing
 * @description:
 * @date: Created in 16:49 2020/9/3
 */
@RequestMapping(value = "kafka")
@RestController
public class ProducerController {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @RequestMapping(value = "/producer", method = RequestMethod.GET)
    public Object consume(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String value = "测试数据";
        ListenableFuture result = kafkaTemplate.send("my-replicated-topic", value);
        return JsonResult.jsonResult("发送结果==");
    }


    /**
     * 从本地上传目录结构的3dtile文件
     *
     * @return
     */
    @GetMapping(value = "/stream")
    public Object kafkaStream() {

        String output = "recommender";  //输出 topic

        Properties properties = new Properties();
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "logProcessor");
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "hadoop1:9092");
        //使用Serdes类创建序列化/反序列化所需的Serde实例 Serdes类为以下类型提供默认的实现：String、Byte array、Long、Integer和Double。
        Serde<String> stringSerde = Serdes.String();

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> simpleFirstStream = builder.stream("topic", Consumed.with(stringSerde, stringSerde));
        // 使用KStream.mapValues 将输入数据流以 abc: 拆分获取下标为 1 字符串
        KStream<String, String> upperCasedStream = simpleFirstStream.mapValues(line -> line.split("abc:")[1]);
        // 把转换结果输出到另一个topic
        upperCasedStream.to(output, Produced.with(stringSerde, stringSerde));

        //创建和启动KStream
        KafkaStreams kafkaStreams = new KafkaStreams(builder.build(), properties);
        kafkaStreams.start();


        return JsonResult.jsonResult("上传成功");
    }

}
