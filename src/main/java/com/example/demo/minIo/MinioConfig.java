package com.example.demo.minIo;

import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: lbing
 * @description:
 * @date: Created in 10:48 2020/8/28
 */
@Slf4j
@Configuration
public class MinioConfig {


    @Autowired
    private MinioData minioData;

    /**
     * 初始化minio客户端,不用每次都初始化
     *
     * @return MinioClient
     * @author 溪云阁
     */
    @Bean
    public MinioClient minioClient() {
        try {

            System.out.println("对象数据："+minioData.toString());
            return MinioClient.builder()
                    .endpoint(minioData.getUrl())
                    .credentials(minioData.getAccessKey(), minioData.getSecretKey())
                    .build();
        } catch (final Exception e) {
            log.error("初始化minio出现异常:{}", e.fillInStackTrace());
            return null;
        }

    }


}
