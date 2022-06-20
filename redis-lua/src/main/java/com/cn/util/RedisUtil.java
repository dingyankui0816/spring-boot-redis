package com.cn.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description Redis Util
 * @Author: Levi.Ding
 * @Date: 2022/6/14 14:48
 * @Version V1.0
 */
@Component
public class RedisUtil {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedisScript<Long> redisScript;

    public Long scriptLoadEval(){

        List<String> keys = new ArrayList<>();
        keys.add("b");
        keys.add("1");

        System.out.println(redisScript.getSha1());
        Object result = redisTemplate.execute(redisScript,keys,"a","1");
        System.out.println(redisTemplate.opsForValue().get("a"));
        System.out.println(result);
        System.out.println(redisScript.getSha1());
        return (Long) (result);
    }
}
