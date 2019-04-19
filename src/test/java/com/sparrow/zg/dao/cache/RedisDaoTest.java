package com.sparrow.zg.dao.cache;

import com.sparrow.zg.dao.SeckillDao;
import com.sparrow.zg.entity.Seckill;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.swing.*;

import static org.junit.Assert.*;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})

public class RedisDaoTest {
	private long id = 1001;
	@Autowired
	private RedisDao redisDao;

	@Autowired
	private SeckillDao seckillDao;

	//使用Redis 获取缓存对象
	@Test
	public void testSeckill() {
		Seckill seckill = redisDao.getSeckill(id);
		if(seckill==null){
			seckill = seckillDao.queryById(id);
			if(seckill!=null){
				String result = redisDao.putSeckill(seckill);
				System.out.println(result);
				seckill = redisDao.getSeckill(id);
				System.out.println(seckill);
			}
		}
	}
}