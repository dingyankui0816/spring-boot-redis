package com.example.demo;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import com.example.demo.service.RedisService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.UnsupportedEncodingException;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {DemoApplication.class})
@WebAppConfiguration
public class RedisTest {
    @Autowired
    private RedisService redisService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void redisTest(){
        new Thread(()-> {
            System.out.println(redisService.cacheBreakdown_2("a"));
        }).start();
        new Thread(()-> {
            System.out.println(redisService.cacheBreakdown_2("a"));
        }).start();
        new Thread(()-> {
            System.out.println(redisService.cacheBreakdown_2("a"));
        }).start();
        new Thread(()-> {
            System.out.println(redisService.cacheBreakdown_2("a"));
        }).start();
        System.out.println(redisService.cacheBreakdown_2("a"));
    }

    @Test
    public void testRedis() throws UnsupportedEncodingException {
        redisPipeline();
        System.out.println("--------------------------------");
        redisNoPipeline();
    }

    public void redisPipeline() throws UnsupportedEncodingException {
        String key = "REDIS:PIPELINE:TEST:1";
        byte[] bytes = key.getBytes("UTF-8");
        long start = System.currentTimeMillis();
        List<Long> a = redisTemplate.executePipelined((RedisCallback<?>) connection -> {
            for (int b =0;b<10000;b++){
                connection.incrBy(bytes,1);
            }
            return null;
        });
        long end = System.currentTimeMillis();
        System.out.println("耗时: "+ (end-start) + "ms;");
    }

    public void redisNoPipeline() throws UnsupportedEncodingException {
        String key = "REDIS:PIPELINE:TEST:2";
        byte[] bytes = key.getBytes("UTF-8");
        long start = System.currentTimeMillis();
        List<Long> a = (List<Long>) redisTemplate.execute(connection -> {
            for (int b =0;b<10000;b++){
                 connection.incrBy(bytes,1);
            }
            return null;
        },true);
        long end = System.currentTimeMillis();
        System.out.println("耗时: "+ (end-start) + "ms;");
    }

    @Test
    public void redisSubscribe() {
        String channel = "aaa";
        redisTemplate.convertAndSend(channel,new String("测试是否成功"));
        redisTemplate.convertAndSend(channel,new String("bbb"));
    }

    @Test
    public void serializer() throws UnsupportedEncodingException {
        FastJsonRedisSerializer fastJsonRedisSerializer = new FastJsonRedisSerializer(Object.class);
        byte[] bytes = fastJsonRedisSerializer.serialize("测试String 类型");
        System.out.println(fastJsonRedisSerializer.deserialize(bytes));
        System.out.println(new String(bytes,"UTF-8"));
    }
}
