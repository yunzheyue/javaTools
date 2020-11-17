package com.example.demo.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.common.JsonResult;
import com.example.demo.entity.Test;
import com.example.demo.service.TestService;
import com.example.demo.service.impl.TestServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lbing
 * @since 2020-11-17
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    TestServiceImpl testServiceImpl;

    @RequestMapping(value = "/select", method = RequestMethod.GET)
    public Object select()  {
        QueryWrapper<Test> testQueryWrapper = new QueryWrapper<>();
        List<Test> list = testServiceImpl.list(testQueryWrapper);

        return JsonResult.jsonResult(list);
    }
}

