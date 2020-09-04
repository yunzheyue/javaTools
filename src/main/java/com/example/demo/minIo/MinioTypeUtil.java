package com.example.demo.minIo;

import org.springframework.http.MediaType;

import java.util.UUID;

/**
 * @author: lbing
 * @description:
 * @date: Created in 14:22 2020/8/28
 */

public class MinioTypeUtil {


    public static String getMediaType(String suffix) throws Exception {
        switch (suffix) {
            case ".png":
                return MediaType.IMAGE_PNG_VALUE;
            case ".jpeg":
            case ".jpg":
                return MediaType.IMAGE_JPEG_VALUE;
            case ".docx":
            case ".doc":
                return MediaType.TEXT_PLAIN_VALUE;
            case ".pdf":
                return MediaType.APPLICATION_PDF_VALUE;
            case ".ppt":
            case ".pptx":
            case ".avi":
            case ".mp4":
            case ".mp3":
                return MediaType.MULTIPART_MIXED_VALUE;
            default:
                return MediaType.MULTIPART_MIXED_VALUE;
        }
    }

    public static String getBucketName(String suffix) {
        switch (suffix) {
            case ".jpg":
            case ".png":
            case ".jpeg":
                return "image";
            case ".docx":
            case ".doc":
            case ".pdf":
                return "word";
            case ".ppt":
            case ".pptx":
                return "ppt";
            case ".avi":
            case ".mp4":
                return "video";
            case ".mp3":
                return "audio";
            default:
                return "default";
        }
    }

    /**
     * 获取文件名
     *
     * @param suffix 后缀
     * @return 返回上传文件名
     */
    public static String getFileName(String suffix) {
        //生成uuid
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        return uuid + suffix;
    }
}
