package com.sparrow.zg.service;

import com.sparrow.zg.entity.Seckill;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
	"classpath:spring/spring-dao.xml",
	"classpath:spring/spring-service.xml"})
public class SeckillServiceTest {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SeckillService seckillService;
	@Test
	public void getSeckillList() {
		System.out.println(seckillService);
		List<Seckill> list = seckillService.getSeckillList();
		logger.info("list={}",list);
	}

	@Test
	public void getById() {
		long id = 1000;
		Seckill seckill = seckillService.getById(id);
//		logger.info("seckill={}",seckill);
	}

	@Test
	public void exportSeckillUrl() {
	}

	@Test
	public void executeSeckill() {
	}
}