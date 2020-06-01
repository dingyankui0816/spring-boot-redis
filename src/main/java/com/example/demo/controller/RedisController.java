package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/aaa")
public class RedisController {
    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(value = "/subscribe",method = RequestMethod.GET)
    public void subscribe(){
        String channel = "aaa";
        redisTemplate.convertAndSend(channel,new String("测试是否成功"));
        redisTemplate.convertAndSend(channel,new String("bbb"));
    }
}
