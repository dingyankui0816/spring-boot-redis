package com.example.demo.config;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import com.example.demo.listener.BaseListener;
import com.example.demo.listener.RedisListener1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;

import java.util.List;


@Configuration
public class RedisConfig {

    @Primary
    @Bean
    public RedisTemplate getRedisTemplate(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setKeySerializer(redisTemplate.getStringSerializer());

        redisTemplate.setValueSerializer(new GenericFastJsonRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericFastJsonRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

    @Bean
    public RedisMessageListenerContainer getRedisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory, List<BaseListener> baseListeners){
        RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
        Topic topic = new ChannelTopic("aaa");
        redisMessageListenerContainer.setConnectionFactory(redisConnectionFactory);
        redisMessageListenerContainer.setRecoveryInterval(3000);
        for (BaseListener baseListener : baseListeners) {
            redisMessageListenerContainer.addMessageListener(baseListener,topic);
        }
        return redisMessageListenerContainer;
    }
}
