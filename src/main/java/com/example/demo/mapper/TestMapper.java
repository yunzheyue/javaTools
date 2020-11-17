package com.example.demo.mapper;

import com.example.demo.entity.Test;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lbing
 * @since 2020-11-17
 */
@Mapper
public interface TestMapper extends BaseMapper<Test> {

}
