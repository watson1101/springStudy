package com.yy.config;

import com.yy.domain.User;
import com.yy.enums.UserSexEnum;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestRedis {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void test() throws Exception {
        stringRedisTemplate.opsForValue().set("aaa", "111");
        Assert.assertEquals("111", stringRedisTemplate.opsForValue().get("aaa"));
        stringRedisTemplate.delete("aaa");
        Assert.assertTrue(stringRedisTemplate.opsForValue().get("aaa")==null);
    }

    @Test
    public void testObj() throws Exception {
        User user= new User("aa", "a123456", UserSexEnum.MAN);
        ValueOperations<String, User> operations=redisTemplate.opsForValue();
        operations.set("com.yy", user);
        operations.set("com.yy.f", user,1, TimeUnit.SECONDS);
        Thread.sleep(1000);
        //redisTemplate.delete("com.neo.f");

        boolean exists1=redisTemplate.hasKey("com.yy");

        if(exists1){
            System.out.println("com.yy is true");
        }else{
            System.out.println("com.yy is false");
        }

        boolean exists=redisTemplate.hasKey("com.yy.f");
        if(exists){
            System.out.println("com.yy.f is true");
        }else{
            System.out.println("com.yy.f is false");
        }
       Object v=  redisTemplate.opsForValue().get("com.yy");
        System.out.println(v);
        redisTemplate.delete("com.yy");
        Assert.assertTrue(redisTemplate.hasKey("com.yy")==false);
        // Assert.assertEquals("aa", operations.get("com.neo.f").getUserName());
    }
}
