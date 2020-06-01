package com.example.demo.listener;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;


@Component
@Slf4j
public class RedisListener1 extends BaseListener<String> {

    @Override
    public void process(String s) {
        log.info("测试listener1 , s : {}",s);
    }
}
