package com.taotao.jedis;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestJedisSpring {
	
	@Test
	public void testJedisClientPool(){
		//初始化Spring容器
		ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-redis.xml");
		//从容器中获取JedisClient对象
		JedisClient jedisClient = ctx.getBean(JedisClient.class);
		//使用JedisClient操作Redis
		jedisClient.set("jedisClient", "mytest");
		String result = jedisClient.get("jedisClient");
		System.out.println(result);
	}

}