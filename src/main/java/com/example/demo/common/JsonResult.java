package com.example.demo.common;

/**
 * @author zhangxujing
 * @date 2020/7/27 11:10
 * @desc
 */
public class JsonResult {

    public static JsonResponse jsonResult() {
        return new JsonResponse();
    }

    public static JsonResponse jsonResult(int status) {
        return new JsonResponse(status);
    }

    public static JsonResponse jsonResult(int status, String msg) {
        return new JsonResponse(status, msg);
    }

    public static JsonResponse jsonResult(Object body) {
        return new JsonResponse(body);
    }

    public static JsonResponse jsonResult(int status, String msg, Object body) {
        return new JsonResponse(status, msg, body);
    }

}
