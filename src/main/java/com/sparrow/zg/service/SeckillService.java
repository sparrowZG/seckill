package com.sparrow.zg.service;

import com.sparrow.zg.dto.Exposer;
import com.sparrow.zg.dto.SeckillExecution;
import com.sparrow.zg.entity.Seckill;
import com.sparrow.zg.exception.RepeatKillException;
import com.sparrow.zg.exception.SeckillException;

import java.util.List;

public interface SeckillService {
	/**
	 * 查询所有秒杀记录
	 * @return
	 */
	List<Seckill> getSeckillList();

	/**
	 *查询单个秒杀记录
	 * @return
	 */
	Seckill getById(long seckilled);

	/**
	 * 输出秒杀接口地址:否则输出系统时间和秒杀时间
	 * @param seckilled
	 */
	Exposer exportSeckillUrl(long seckilled);

	/**
	 *
	 * @param seckilled
	 * @param userPhone
	 * @param md5
	 */
	SeckillExecution executeSeckill(long seckilled, long userPhone, String md5)
	throws SeckillException,RepeatKillException,SeckillException;
}
