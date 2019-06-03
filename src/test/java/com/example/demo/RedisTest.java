package com.example.demo;

import com.example.demo.service.RedisService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {DemoApplication.class})
@WebAppConfiguration
public class RedisTest {
    @Autowired
    private RedisService redisService;

    @Test
    public void redisTest(){
        new Thread(()-> {
            System.out.println(redisService.cacheBreakdown_2("STR5","LOCAL"));
        }).start();
        new Thread(()-> {
            System.out.println(redisService.cacheBreakdown_2("STR6","LOCAL"));
        }).start();
        new Thread(()-> {
            System.out.println(redisService.cacheBreakdown_2("STR7","LOCAL"));
        }).start();
        new Thread(()-> {
            System.out.println(redisService.cacheBreakdown_2("STR8","LOCAL"));
        }).start();
        System.out.println(redisService.cacheBreakdown_2("STR9","LOCAL"));
    }
}
