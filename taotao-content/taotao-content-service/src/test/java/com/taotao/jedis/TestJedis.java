package com.taotao.jedis;

import org.junit.Test;

import redis.clients.jedis.Jedis;


public class TestJedis {
	@Test
	public void testJedis(){
		
		Jedis jedis = new Jedis("192.168.136.129",6379);
		
		//jedis.set
		//jedis.close();
		
	}
}
