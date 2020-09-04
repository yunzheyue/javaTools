package com.example.demo.common;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author zxj
 * @create 2020/7/26
 * @desc
 */
public class JsonResponse {

    private JsonResponseHeader header;
    private Object body;


    public JsonResponse() {
        this(ResponseStatusEnum.OK.status, ResponseStatusEnum.OK.msg, null);
    }

    public JsonResponse(int status) {
        this(status,  ResponseStatusEnum.getEnum(status) == null ? "" :  ResponseStatusEnum.getEnum(status).msg, null);
    }

    public JsonResponse(int status, String msg) {
        this(status, msg,null);
    }

    public JsonResponse(Object body) {
        this(ResponseStatusEnum.OK.status, ResponseStatusEnum.OK.msg, body);
    }

    public JsonResponse(int status, String msg, Object body) {
        this.header = new JsonResponseHeader(status, msg);
        if (body == null) {
            body = new HashMap();
        }
        this.body = body;
    }

    public JsonResponse(String key, Object value) {
        Map<String, Object> body = new HashMap<>();
        body.put(key, value);

        this.header = new JsonResponseHeader(ResponseStatusEnum.OK.status, ResponseStatusEnum.OK.msg);
        this.body = body;
    }

    public JsonResponseHeader getHeader() {
        return header;
    }

    public void setHeader(JsonResponseHeader header) {
        this.header = header;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }


    /**
     * 业务层 响应头对象
     */
    public class JsonResponseHeader {

        private int status;
        private String msg;

        public JsonResponseHeader(int status, String msg) {
            this.status = status;
            this.msg = msg;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }

    /**
     * 响应状态枚举
     */
    public enum ResponseStatusEnum {

        OK(200, "OK"),
        INVALID_SESSION(400, "Request fail: invalid session"),
        UNAUTHORIZED(401, "Request fail: unauthorized"),
        ERROR(500, "Request fail: unexpected error"),
        INVALID_PARAMETER(600, "Request fail: invalid parameter"),
        INVALID_SIGN(601, "Request fail: invalid sign"),
        REQUEST_EXPIRED(602, "Request expired"),
        INVALID_PARAMETER_COUNT(603, "Request fail: 无效的请求数量值"),
        INVALID_PARAMETER_TIME(604, "Request fail: 无效的时间范围参数"),
        PARAMETER_IS_NOT_PRESENT(605, "Request fail: parameter is not present");


        private int status;
        private String msg;

        ResponseStatusEnum(int status, String msg) {
            this.status = status;
            this.msg = msg;
        }

        public int getStatus() {
            return status;
        }

        public String getMsg() {
            return msg;
        }

        public static ResponseStatusEnum getEnum(int status) {
            Optional<ResponseStatusEnum> optional = Arrays.stream(ResponseStatusEnum.values())
                    .filter(t -> t.getStatus() == status)
                    .findFirst();
            return optional != null ? optional.get() : null;
        }

    }

}
