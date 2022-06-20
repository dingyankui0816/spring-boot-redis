package com.cn.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

/**
 * @Description todo
 * @Author: Levi.Ding
 * @Date: 2022/6/14 15:33
 * @Version V1.0
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedisScript<Long> getRedisScript(){
        DefaultRedisScript<Long> defaultRedisScript = new DefaultRedisScript<>();
        defaultRedisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("test.lua")));
        defaultRedisScript.setResultType(Long.class);
        return defaultRedisScript;
    }



}
