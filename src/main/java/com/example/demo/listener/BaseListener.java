package com.example.demo.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;

public abstract class BaseListener<T> implements MessageListener {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        Object o = redisTemplate.getValueSerializer().deserialize(message.getBody());
        process((T)o);
    }

    public abstract void process(T t);
}
