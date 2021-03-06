package com.sparrow.zg.dao;

import com.sparrow.zg.entity.SuccessKilled;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/spring-dao.xml")
public class SuccessKilledDaoTest {

	@Resource
	private SuccessKilledDao successKilledDao;
	@Test
	public void insertSuccessKilled() {
		long id = 1000L;
		long phone = 18792388272L;
		int insertCount = successKilledDao.insertSuccessKilled(id,phone);
		System.out.println("insertCount="+insertCount);
	}

	@Test
	public void queryByIdWithSeckill() {
		long id = 1000L;
		long phone = 18792388272L;
		SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(id,phone);

		System.out.println(successKilled);
		System.out.println(successKilled.getSeckill());
	}
}