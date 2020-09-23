package com.example.demo.redislock;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author zhangxujing
 * @date 2020/7/21 14:13
 * @desc 配置redis连接
 */
@Configuration
public class RedisTemplateConfig {

    @Value("${spring.redis.database}")
    private String oneDatabase;
    @Value("${spring.redis.host}")
    private String oneHost;

    @Value("${spring.redis.port}")
    private String onePort;

    @Value("${spring.redis.password}")
    private String onePassword;


    /**
     * 最大连接数
     */
    @Value("${spring.redis.jedis.pool.max-active}")
    private int MAX_ACTIVE;
    /**
     * 最大空闲连接数
     */
    @Value("${spring.redis.jedis.pool.max-idle}")
    private int MAX_IDLE;
    /**
     * 最小空闲连接数
     */
    @Value("${spring.redis.jedis.pool.min-idle}")
    private int MIN_IDLE;
    /**
     * 连接超时时间毫秒
     */
    @Value("${spring.redis.timeout}")
    private long MAX_WAIT_MILLIS;

    /**
     * 第一个连接
     *
     * @return
     */
    @Primary
    @Bean(name = "redisOneTemplate")
    public RedisTemplate<String, Object> redisOneTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        redisSerializeConfig(template);
        template.setConnectionFactory(connectionFactory(oneHost, Integer.parseInt(onePort), onePassword, MAX_IDLE, MIN_IDLE, MAX_ACTIVE, MAX_WAIT_MILLIS, Integer.parseInt(oneDatabase)));
        return template;
    }


    //配置工厂
    public RedisConnectionFactory connectionFactory(String host, int port, String password, int maxIdle, int minIdle,
                                                    int maxActive, long maxWaitMillis, int index) {
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
        jedisConnectionFactory.setHostName(host);
        jedisConnectionFactory.setPort(port);

        if (!StringUtils.isEmpty(password)) {
            jedisConnectionFactory.setPassword(password);
        }
        if (index != 0) {
            jedisConnectionFactory.setDatabase(index);
        }

        jedisConnectionFactory.setPoolConfig(poolConfig(maxIdle, maxActive, minIdle, maxWaitMillis));
        jedisConnectionFactory.afterPropertiesSet();
        return jedisConnectionFactory;
    }

    //连接池配置
    public JedisPoolConfig poolConfig(int maxIdle, int maxActive, int minIdle, long maxWaitMillis) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMaxTotal(maxActive);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setMaxWaitMillis(maxWaitMillis);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setNumTestsPerEvictionRun(10);
        poolConfig.setTimeBetweenEvictionRunsMillis(60000);
        return poolConfig;
    }


    public void redisSerializeConfig(RedisTemplate<String, Object> template) {
        Jackson2JsonRedisSerializer<Object> redisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        redisSerializer.setObjectMapper(om);
        //value 采用  Jackson2JsonRedisSerializer
        template.setValueSerializer(redisSerializer);
        //key 采用 stringRedisSerializer
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        template.setHashValueSerializer(stringRedisSerializer);

        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);
    }


}
