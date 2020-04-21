package com.example.demo.service;


import com.google.common.collect.Maps;

import com.alibaba.fastjson.JSON;
import com.example.demo.async.RedisAsync;
import com.example.demo.common.RedisConstant;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class RedisService {
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private RedisAsync redisAsync;

    private Lock lock = new ReentrantLock();

    /**
     * 缓存击穿,雪崩解决，使用线程锁，保证同一时刻只能有同一种key值能够进行 db数据的获取
     * @param key 缓存键
     * @return
     * @throws InterruptedException
     */
    public String cacheBreakdown_1(String key) throws InterruptedException {
            Object value = redisTemplate.opsForValue().get(key);
            if (value != null) {
                return value.toString();
            }
            if (lock.tryLock()) {
                String dataBaseValue;
                try {
                    //数据库获取，并存在redis 中
                    dataBaseValue = "数据库中的值";
                    System.out.println("互斥锁 -- 获取数据库中的值");
                    redisTemplate.opsForValue().set(key,dataBaseValue,60,TimeUnit.MINUTES);
                } finally {
                    lock.unlock();
                }
                return dataBaseValue;
            }
            TimeUnit.SECONDS.sleep(5);
            return cacheBreakdown_1(key);
    }

    /**
     * 缓存穿透 设置空对象
     * @param key 缓存键
     * @return
     */
    public String cachePenetrate_1(String key){
        Object value = redisTemplate.opsForValue().get(key);
        if ("".equals(value)){
            return null;
        }
        if (value!=null){
            return  value.toString();
        }
        String dataBaseValue = "数据库中的值";
        System.out.println("空对象 -- 获取数据库中的值");
        if (dataBaseValue==null){
            redisTemplate.opsForValue().set(key,"",60,TimeUnit.SECONDS);
        }else{
            redisTemplate.opsForValue().set(key,dataBaseValue,60,TimeUnit.MINUTES);
        }
        return dataBaseValue;
    }

    /**
     * 缓存击穿,雪崩解决 ，分布式锁，保证同一时刻只能有同一种key值能够进行 db数据的获取
     * @param key 缓存键
     * @return
     */
    public String cacheBreakdown_2(String key,String localKey){
        System.out.println("线程开始");
        Object value = redisTemplate.opsForValue().get(key);
        if (value!=null){
            return value.toString();
        }
        Boolean isSuccess=redisTemplate.opsForValue().setIfAbsent(localKey,"easy");
        System.out.println(Thread.currentThread().getName()+"isSuccess : "+isSuccess);
        if (isSuccess){
            //数据库获取信息，并放进缓存中
            String dataBaseValue = "数据库中的值";
            System.out.println("分布式锁 -- 获取数据库中的值");
            redisTemplate.opsForValue().set(key,dataBaseValue,60,TimeUnit.MINUTES);
            //todo  增加try catch 保证锁一定会被删除
            redisTemplate.delete(localKey);
            return dataBaseValue;
        }else{
            try {
                System.out.println(Thread.currentThread().getName()+"\t开始等待");
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
           return  cacheBreakdown_2(key,localKey);
        }
    }

    /**
     * 缓存穿透 布隆过滤器
     * @param key 缓存键
     * @return
     */
    public String cachePenetrate_2(String key){
        if(RedisConstant.bloomFilter.mightContain(key)){
            Object value = redisTemplate.opsForValue().get(key);
            if (value!=null){
                return  value.toString();
            }
            String dataBaseValue = "数据库中的值";
            System.out.println("布隆过滤器 -- 获取数据库中的值");
            redisTemplate.opsForValue().set(key,dataBaseValue,60,TimeUnit.MINUTES);
            return dataBaseValue;
        }else{
            System.out.println("该key不存在");
            return null;
        }
    }

    /**
     * 雪崩解决 设置永久性，并在value中添加过期时间，用于过期更新key值操作
     * @param key 缓存键
     * @return
     */
    public String cacheBreakdown_3(String key){
        Object value = redisTemplate.opsForValue().get(key);
        if (value!=null){
            Map<String,String> map=JSON.parseObject(value.toString(),HashMap.class);
            LocalDateTime ldt=LocalDateTime.parse(map.get("expire"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            if (ldt.isBefore(LocalDateTime.now())){
                redisAsync.refreshAsync(key,map);
            }
            return map.get("value");
        }else{
            Map<String,String> map= Maps.newHashMap();
            map.put("value","数据库中的值");
            map.put("expire","2019-04-17 11:30:00");
            System.out.println("永久性 -- 获取数据库中的值");
            String dataBaseValue = JSON.toJSONString(map);
            redisTemplate.opsForValue().set(key,dataBaseValue);
            return dataBaseValue;
        }
    }

    /**
     * 雪崩解决 增加随机过期时间，减少缓存同时消失的概率
     * @param key 缓存键
     * @return
     */
    public String cacheBreakdown_4(String key){
        Object value = redisTemplate.opsForValue().get(key);
        if (value!=null){
            return value.toString();
        }
        String dataBaseValue = "数据库中的值";
        Random random=new Random(100);
        System.out.println("随机值 -- 获取数据库中的值");
        redisTemplate.opsForValue().set(key,dataBaseValue,60+ random.nextInt(6),TimeUnit.MINUTES);
        return dataBaseValue;
    }

    /**
     * 雪崩解决 , 缓存击穿 hystrix 降级操作
     * @param key
     * @return
     */
    @HystrixCommand(fallbackMethod = "hystrixString")
    public String cacheBreakdown_5(String key) throws Exception {
        Object value = redisTemplate.opsForValue().get(key);
        if (value!=null){
            return value.toString();
        }
        String dataBaseValue="数据库中的值";
        System.out.println("hystrix -- 获取数据库中的值");
        redisTemplate.opsForValue().set(key,dataBaseValue,60, TimeUnit.MINUTES);
        throw new Exception("adfs");
    }

    public String hystrixString(String key){
        return "服务器繁忙！";
    }
}
