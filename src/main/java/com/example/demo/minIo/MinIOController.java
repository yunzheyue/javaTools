package com.example.demo.minIo;

import com.example.demo.common.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;


/**
 * @author: lbing
 * @description:
 * @date: Created in 11:04 2020/8/28
 */

@RestController
@RequestMapping("minio")
public class MinIOController {

    @Autowired
    MinIOService minIOService;

    /**
     * @param folders
     * @return
     */
    @PostMapping(value = "/3dtile/web/upload")
    public Object saveMultiFile(MultipartFile[] folders) {
        if (folders == null || folders.length == 0) {
            return JsonResult.jsonResult("上传文件有误");
        }
//        解决开启线程后找不到inputStream
        ArrayList<FileIO> ioList = new ArrayList<FileIO>();

        for (MultipartFile file : folders) {
            try {
                ioList.add(new FileIO(file.getInputStream(), file.getOriginalFilename()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("开始上传");
        new Thread(() -> {
            String url = minIOService.saveMultiFileFromNet("", ioList);
            System.out.println("url==" + url);
        }).start();

        return JsonResult.jsonResult("上传成功");
    }

    /**
     * 从本地上传目录结构的3dtile文件
     * @return
     */
    @GetMapping(value = "/3dtile/local/upload")
    public Object saveMultiFileByLocal(@RequestParam(name = "path",defaultValue = "/HD/3DTiles_all",required = false) String  path) {
        System.out.println("path=="+path);
        new Thread(()->{
            System.out.println("开始上传...");
            long l = System.currentTimeMillis();
            String url = minIOService.saveMultiFileFromLocal(path,"");
            System.out.println("上传成功！url=="+url+"  time="+(System.currentTimeMillis()-l));
        }).start();

        return JsonResult.jsonResult("上传成功");
    }







}
