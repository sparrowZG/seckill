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
	 * @param seckillId
	 * @param userPhone
	 * @param md5
	 */
	SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
	throws SeckillException,RepeatKillException,SeckillException;

	/**
	 * 存储过程
	 * @param seckillId
	 * @param userPhone
	 * @param md5
	 * @return
	 * @throws SeckillException
	 * @throws RepeatKillException
	 * @throws SeckillException
	 */
	SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5);

}
