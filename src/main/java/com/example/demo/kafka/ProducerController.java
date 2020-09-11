package com.example.demo.kafka;

import com.example.demo.common.JsonResult;
import com.example.demo.minIo.MinioTypeUtil;
import io.minio.PutObjectArgs;
import io.minio.PutObjectOptions;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.kafka.common.serialization.*;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Future;

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
//        ListenableFuture result = kafkaTemplate.send("my-replicated-topic", value);
        kafkaTemplate.send("my-replicated-topic", "111", "123");
        return JsonResult.jsonResult("发送结果==");
    }


    @GetMapping(value = "/stream")
    public Object kafkaStream() {

        String output = "recommender";  //输出 topic

//        Map<String, Object> configurationProperties = kafkaTemplate.getProducerFactory().getConfigurationProperties();

        Properties properties = new Properties();
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "10.170.58.160:9092");
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

    @GetMapping(value = "/file1")
    public Object kafkaFile1(@RequestParam(name = "path", defaultValue = "/HD/3DTiles_all", required = false) String path) throws Exception {


        new Thread(() -> {
            long l = System.currentTimeMillis();
            Properties props = new Properties();
            //kafka brokerlist
            props.put("bootstrap.servers", "10.170.58.160:9092");
            //ack "0,1,-1 all"四种，1为文件patition leader完成写入就算完成
            props.put("acks", "1");
            props.put("retries", 0);
            props.put("batch.size", 16384);
            props.put("linger.ms", 1);
            props.put("max.request.size", 126951500);
            props.put("buffer.memory", 335544320);
            //必须设置(k,v)的序列化  详情见kafkaProducer 的构造函数
            props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
            KafkaProducer<Object, Object> producer = new KafkaProducer<>(props);
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            saveMultiFileFromLocal1(path, producer, "");
            producer.close();
            System.out.println("花费时间：" + (System.currentTimeMillis() - l));

        }).start();

        return JsonResult.jsonResult("上传成功");
    }

    /**
     * 上传本地目录下文件
     *
     * @param path    文件路径
     * @param filePre 文件的前缀，比如存放在mesh下
     * @return
     */
    public String saveMultiFileFromLocal1(String path, KafkaProducer<Object, Object> producer, String filePre) {

//        String  replaceStr= path.replaceAll("\\\\", "/");  //linux
//        String filePack = replaceStr.substring(replaceStr.lastIndexOf("/")+1);//获取最外层的文件夹名称  linux

        String filePack = path.substring(path.lastIndexOf(File.separator) + 1);//获取最外层的文件夹名称  windows

        System.out.println("最外层的文件夹名称:" + filePack);
        File destFile = new File(path);
        try {
            traverseFolder1(filePack, destFile, producer, destFile.getName(), filePre);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    String tempString = null;

    private void traverseFolder1(String filePack, File file, KafkaProducer<Object, Object> producer, String originalName, String filePre) throws Exception {
        File[] fs = file.listFiles();
        for (File f : fs) {
            if (f.isDirectory())    //若是目录，则递归打印该目录下的文件
                traverseFolder1(filePack, f, producer, originalName, filePre);
            if (f.isFile()) { //若是文件
                String fileName = f.getCanonicalPath().split(filePack)[1];//windows
//                System.out.println("文件路径："+f.getName()+"   "+fileName );
                Future<RecordMetadata> send = producer.send(new ProducerRecord<>("my-replicated-topic", filePack + fileName, fileConvertToByteArray(f)));
//                System.out.println("发送数据："+send.get().toString());
            }

        }
    }

    private byte[] fileConvertToByteArray(File file) {
        byte[] data = null;

        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            int len;
            byte[] bytes = new byte[fis.available()];
//            byte[] bytes = new byte[10*1024*1024];

            while ((len = fis.read(bytes)) != -1) {
                baos.write(bytes, 0, len);
            }

            data = baos.toByteArray();

            fis.close();
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    @GetMapping(value = "/file")
    public Object kafkaFile(@RequestParam(name = "path", defaultValue = "/HD/3DTiles_all", required = false) String path) throws Exception {


        new Thread(() -> {
            long l = System.currentTimeMillis();
            Properties props = new Properties();
            //kafka brokerlist
            props.put("bootstrap.servers", "10.170.58.160:9092");
            //ack "0,1,-1 all"四种，1为文件patition leader完成写入就算完成
            props.put("acks", "1");
            props.put("retries", 0);
            props.put("batch.size", 16384);
            props.put("linger.ms", 1);
            props.put("buffer.memory", 33554432);
            //必须设置(k,v)的序列化  详情见kafkaProducer 的构造函数
            props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            KafkaProducer<Object, Object> producer = new KafkaProducer<>(props);

            saveMultiFileFromLocal(path, producer, "");
            producer.close();
            System.out.println("花费时间：" + (System.currentTimeMillis() - l));

        }).start();

        return JsonResult.jsonResult("上传成功");
    }


    /**
     * 上传本地目录下文件
     *
     * @param path    文件路径
     * @param filePre 文件的前缀，比如存放在mesh下
     * @return
     */
    public String saveMultiFileFromLocal(String path, KafkaProducer<Object, Object> producer, String filePre) {

//        String  replaceStr= path.replaceAll("\\\\", "/");  //linux
//        String filePack = replaceStr.substring(replaceStr.lastIndexOf("/")+1);//获取最外层的文件夹名称  linux

        String filePack = path.substring(path.lastIndexOf(File.separator) + 1);//获取最外层的文件夹名称  windows

        System.out.println("最外层的文件夹名称:" + filePack);
        File destFile = new File(path);
        try {
            traverseFolder(filePack, destFile, producer, destFile.getName(), filePre);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    private String url = "";

    /**
     * 递归查询所有文件
     *
     * @param file
     * @param originalName 最外层的文件夹名称
     * @param filePre      文件的前缀，比如存放在mesh下
     * @throws Exception
     */

    private void traverseFolder(String filePack, File file, KafkaProducer<Object, Object> producer, String originalName, String filePre) throws Exception {
        File[] fs = file.listFiles();
        for (File f : fs) {
            if (f.isDirectory())    //若是目录，则递归打印该目录下的文件
                traverseFolder(filePack, f, producer, originalName, filePre);
            if (f.isFile()) { //若是文件
                FileInputStream fis = new FileInputStream(f);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader br = new BufferedReader(isr);
                int count = 0;
                while ((tempString = br.readLine()) != null) {
//                    String fileName = f.getCanonicalPath().replaceAll("\\\\", "/").split(filePack)[1];//linux
                    String fileName = f.getCanonicalPath().split(filePack)[1];//windows
//                    System.out.println("发送文件路径：" + fileName);
                    producer.send(new ProducerRecord<>("my-replicated-topic", filePack + fileName + "&&" + count, tempString));
                    count++;
                    //发送完成后打印发送数据
//                    System.out.println(tempString);
                }
            }

        }
    }




}
