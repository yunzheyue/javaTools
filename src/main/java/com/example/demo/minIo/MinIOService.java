package com.example.demo.minIo;

import io.minio.*;
import io.minio.http.Method;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;

import static com.example.demo.common.JsonResult.jsonResult;


/**
 * @author: lbing
 * @description:
 * @date: Created in 14:00 2020/8/28
 */
@Service
public class MinIOService {


    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MinioData minioData;

    /**
     * 上传单个文件
     *
     * @param file
     * @param filePre 文件名前缀
     * @return
     * @throws Exception
     */
    public String uploadFile(MultipartFile file, String filePre) throws Exception {
        if (file.isEmpty()) {
            return "文件名不正确";
        } else {


            InputStream is = file.getInputStream();
//            String fileName = file.getOriginalFilename();
            String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            String fileName = filePre + MinioTypeUtil.getFileName(suffix);
            String mediaType = MinioTypeUtil.getMediaType(suffix);
            String bucketName = MinioTypeUtil.getBucketName(suffix);

            boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());

            if (isExist) {
                System.out.println("Bucket already exists.");
            } else {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }

            // 把文件放到minio的boots桶里面
            PutObjectOptions putObjectOptions = new PutObjectOptions(is.available(), -1);
            PutObjectArgs build = PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .contentType(mediaType)
                    .stream(is, putObjectOptions.objectSize(), putObjectOptions.partSize())
                    .contentType(putObjectOptions.contentType())
                    .headers(putObjectOptions.headers())
                    .sse(putObjectOptions.sse())
                    .build();
            minioClient.putObject(build);
            // 关闭输入流
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String url = getObectUrl(fileName, bucketName, false);
            return url;
        }
    }


    /**
     * 删除文件
     *
     * @param fileName
     * @return
     * @throws Exception
     */
    public Object delete(String fileName, String bucketName) throws Exception {
        RemoveObjectArgs build = RemoveObjectArgs.builder()
                .bucket(bucketName)
                .object(fileName)
                .build();
        minioClient.removeObject(build);
        return jsonResult("删除成功");
    }

    /**
     * @param fileName
     * @param response
     * @throws Exception
     */
    public void download(String fileName, String bucketName,HttpServletResponse response) throws Exception {
        StatObjectArgs build = StatObjectArgs.builder().bucket(minioData.getDefaultBkN()).object(fileName).build();
        final ObjectStat stat = minioClient.statObject(build);
        response.setContentType(stat.contentType());
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        GetObjectArgs getObjectArgs = GetObjectArgs.builder().bucket(bucketName).object(fileName).build();
        InputStream in = minioClient.getObject(getObjectArgs);
        IOUtils.copy(in, response.getOutputStream());
        in.close();
    }

    /**
     * @param fileName
     * @param withLimit url是否携带权限
     * @return
     * @throws Exception
     */
    public String getObectUrl(String fileName, String bucketName, boolean withLimit) throws Exception {
        String url = "";
        if (withLimit) {
            //            携带权限的url
            url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(fileName)
                            .expiry(24 * 60 * 60)//控制在一天时间
                            .build());
        } else {
            //            未携带权限的url
            url = minioClient.getObjectUrl(bucketName, fileName);
        }
        return url;
    }


    /**
     * 上传多个文件
     * 在basePath下保存上传的文件夹
     */
    public String saveMultiFileFromNet(String filePre, ArrayList<FileIO> ioList) {

        String url = "";
        InputStream is = null;

        try {
            boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(minioData.getPointCloudBkN()).build());
            if (isExist) {
                System.out.println("Bucket already exists.");
            } else {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(minioData.getPointCloudBkN()).build());
            }

            for (FileIO file : ioList) {

                is = file.getInputStream();
//            String fileName = file.getOriginalFilename();
                String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
                String fileName = filePre + file.getOriginalFilename();
                String mediaType = MinioTypeUtil.getMediaType(suffix);
                // 把文件放到minio的boots桶里面
                PutObjectOptions putObjectOptions = new PutObjectOptions(is.available(), -1);
                PutObjectArgs build = PutObjectArgs.builder()
                        .bucket(minioData.getPointCloudBkN())
                        .object(fileName)
                        .contentType(mediaType)
                        .stream(is, putObjectOptions.objectSize(), putObjectOptions.partSize())
                        .contentType(putObjectOptions.contentType())
                        .headers(putObjectOptions.headers())
                        .sse(putObjectOptions.sse())
                        .build();
                minioClient.putObject(build);
                System.out.println("上传完毕=" + file.getOriginalFilename());
                if (url.isEmpty()
                        && ".json".equals(suffix)) {
                    url = getObectUrl(filePre + file.getOriginalFilename(), minioData.getPointCloudBkN(),false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            // 关闭输入流
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return url;
    }

    /**
     * 上传本地目录下文件
     *
     * @param path    文件路径
     * @param filePre 文件的前缀，比如存放在mesh下
     * @return
     */
    public String saveMultiFileFromLocal(String path, String filePre) {
        File destFile = new File(path);
        try {
            traverseFolder(destFile, destFile.getName(), filePre);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    private InputStream is;
    private String url = "";

    /**
     * 递归查询所有文件
     *
     * @param file
     * @param originalName 最外层的文件夹名称
     * @param filePre      文件的前缀，比如存放在mesh下
     * @throws Exception
     */
    private void traverseFolder(File file, String originalName, String filePre) throws Exception {
        File[] fs = file.listFiles();
        for (File f : fs) {
            if (f.isDirectory())    //若是目录，则递归打印该目录下的文件
                traverseFolder(f, originalName, filePre);
            if (f.isFile()) { //若是文件，直接打印
                is = new FileInputStream(f);
                String suffix = f.getName().substring(f.getName().lastIndexOf("."));
                String fileName = (filePre + originalName + f.getCanonicalPath().split(originalName)[1]).replaceAll("\\\\", "/");
                String mediaType = MinioTypeUtil.getMediaType(suffix);
                // 把文件放到minio的boots桶里面
                PutObjectOptions putObjectOptions = new PutObjectOptions(is.available(), -1);
                PutObjectArgs build = PutObjectArgs.builder()
                        .bucket(minioData.getPointCloudBkN())
                        .object(fileName)
                        .contentType(mediaType)
                        .stream(is, putObjectOptions.objectSize(), putObjectOptions.partSize())
                        .contentType(putObjectOptions.contentType())
                        .headers(putObjectOptions.headers())
                        .sse(putObjectOptions.sse())
                        .build();
                minioClient.putObject(build);
                System.out.println("上传文件成功：fileName==" + fileName);
                if (".json".equals(suffix) && fileName.split("/").length == 2) {
                    url = getObectUrl(filePre + fileName, minioData.getPointCloudBkN(), false);
                }
            }

        }
    }
}
