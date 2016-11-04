package com.yuncj.redis;

import java.net.MalformedURLException;
import java.net.URL;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class TestSpringRedis {
	
	@Autowired
	protected RedisTemplate<String,Object> redisTemplate;
	
    @Resource(name="redisTemplate")
    private ListOperations<String, String> listOps;

    @Test
    public void addLink() throws MalformedURLException {
    	String userId = "baidu";
    	URL url = new URL("http://www.baidu.com");
        listOps.leftPush(userId, url.toExternalForm());
        
        // or use template directly
        redisTemplate.boundListOps(userId).leftPush(url.toExternalForm());
    }
	
}
