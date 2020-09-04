package com.example.demo.minIo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author: lbing
 * @description:
 * @date: Created in 10:47 2020/8/28
 */

@Data
@Configuration
@ConfigurationProperties(prefix = "boots.module.minio")
public class MinioData {


    /**
     * minio地址+端口号
     */
    private String url;

    /**
     * minio用户名
     */
    private String accessKey;

    /**
     * minio密码
     */
    private String secretKey;

    /**
     * 默认文件桶的名称
     */
    private String defaultBkN;
    /**
     * 点云文件桶的名称
     */
    private String pointCloudBkN;



}
