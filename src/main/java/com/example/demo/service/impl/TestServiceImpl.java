package com.example.demo.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.example.demo.entity.Test;
import com.example.demo.mapper.TestMapper;
import com.example.demo.service.TestService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lbing
 * @since 2020-11-17
 */
@Service
@DS("slave")
public class TestServiceImpl extends ServiceImpl<TestMapper, Test> implements TestService {

}
