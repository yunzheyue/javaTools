package com.example.demo.minIo;

import lombok.Data;

import java.io.InputStream;

/**
 * @author: lbing
 * @description:
 * @date: Created in 16:15 2020/9/1
 */

@Data
public class FileIO {

    private InputStream inputStream;
    private  String originalFilename;

    public FileIO(InputStream inputStream, String originalFilename) {
        this.inputStream = inputStream;
        this.originalFilename = originalFilename;
    }
}
