package com.example.demo.common;

import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

public class RedisConstant {

   public static  BloomFilter<String> bloomFilter=
           BloomFilter.create(Funnels.stringFunnel(Charsets.UTF_8),100000,0.00001);

   static {
       bloomFilter.put("KEY1");
       bloomFilter.put("KEY2");
       bloomFilter.put("KEY3");
   }
   private RedisConstant() throws Exception {
       throw new Exception("failed");
   }
}
