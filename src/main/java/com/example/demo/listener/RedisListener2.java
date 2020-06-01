package com.example.demo.listener;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RedisListener2  extends BaseListener<String>{

    @Override
    public void process(String s) {
        log.info("测试listener2 , s : {}",s);
    }
}
