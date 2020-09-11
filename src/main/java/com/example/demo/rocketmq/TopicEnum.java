package com.example.demo.rocketmq;

/**
 * @author lbing
 * @date 2020/09/11 10:27
 * @describe xxx
 */
public  enum TopicEnum {

        DemoTopic("TopicTest","示例主题"),
        DemoNewTopic("DemoNewTopic","示例主题新"),
        ;

        private String code;
        private String msg;

        private TopicEnum(String code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public String getCode() {
            return this.code;
        }

        public String getMsg() {
            return this.msg;
        }

    }