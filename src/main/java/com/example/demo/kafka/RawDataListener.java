package com.example.demo.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.logging.Logger;

/**
 * @author: lbing
 * @description:
 * @date: Created in 16:50 2020/9/3
 */
@Component
public class RawDataListener {

    /**
     * 实时获取kafka数据(生产一条，监听生产topic自动消费一条)  单组接手
     *
     * @param record
     * @throws IOException
     */
//    @KafkaListener(topics = {"${kafka.consumer.topic}"})
//    public void listen(ConsumerRecord<?, ?> record) throws IOException {
//        System.out.println("不分组数据：key:" + record.key() + " value:" + record.value());
//    }


    /**
     * 将字节流转化成文件
     * @throws IOException
     */
    @KafkaListener(topics = {"${kafka.consumer.topic}"})
    public void listen(ConsumerRecord<?, byte[]> record) {
//        System.out.println("record.key():" + record.key().toString()+"  ");
        long l = System.currentTimeMillis();
        System.out.println("1" );
        try {
            String path = "D:\\kafka_data2" + File.separator + record.key().toString();
//            System.out.println("path:" + path);

            File file = new File(path);
            if (!file.exists()) {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                file.createNewFile();
            }
            writeToFileByByte(file, record.value());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     *
     * @param data
     * @throws Exception
     */
    public static void writeToFileByByte(File file,byte [] data)throws Exception{
        if(data != null){
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data,0,data.length);
            fos.flush();
            fos.close();
        }
    }


    /**
     * 将字符串转化成文件
     * @throws IOException
     */
//    @KafkaListener(topics = {"${kafka.consumer.topic}"})
//    public void listen(ConsumerRecord<?, ?> record) {
//        System.out.println("record.key():" + record.key().toString());
//        long l = System.currentTimeMillis();
//        System.out.println("1" );
//        try {
//            String[] split = record.key().toString().split("&&");
//            String name = split[0];
//            String count = split[1];
//            String path = "D:\\kafka_data2" + File.separator + name;
////            System.out.println("path:" + path);
//
//            File file = new File(path);
//            if (!file.exists()) {
//                if (!file.getParentFile().exists()) {
//                    file.getParentFile().mkdirs();
//                }
//                file.createNewFile();
//            } else {
//                if ("0".equals(count)) {
//                    file.delete();
//                    file.createNewFile();
//                }
//            }
//            writeToFileByStr(file, record.value().toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        System.out.println("写入时间：" + (System.currentTimeMillis() - l));
//    }

    /**
     * 将字符串写入到文件
     * @param file
     * @param _sContent
     * @throws IOException
     */
    public static void writeToFileByStr(File file, String _sContent)

            throws IOException {

        FileWriter fw = null;

        try {
            fw = new FileWriter(file, true);
            fw.write(_sContent);
            fw.write("\n");
            fw.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (fw != null) {
                fw.close();
                fw = null;
            }
        }

    }

}
