package com.sparrow.zg.service.impl;

import com.sparrow.zg.dao.SeckillDao;
import com.sparrow.zg.dao.SuccessKilledDao;
import com.sparrow.zg.dao.cache.RedisDao;
import com.sparrow.zg.dto.Exposer;
import com.sparrow.zg.dto.SeckillExecution;
import com.sparrow.zg.entity.Seckill;
import com.sparrow.zg.entity.SuccessKilled;
import com.sparrow.zg.enums.SeckillStateEnum;
import com.sparrow.zg.exception.RepeatKillException;
import com.sparrow.zg.exception.SeckillCloseException;
import com.sparrow.zg.exception.SeckillException;
import com.sparrow.zg.service.SeckillService;
import com.sun.xml.internal.ws.policy.PolicyMapUtil;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SeckillServiceImpl implements SeckillService {
	//缓存优化
	@Autowired
	private RedisDao redisDao;

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	//注入Service依赖
	@Autowired
	private SeckillDao seckillDao;

	@Autowired
	private SuccessKilledDao successKilledDao;

	private long seckillId;
	//md5混淆参数
	private final String slat = "asdhgoq679876987las(*^*&%&$^%$%$#%^$";

	@Override
	public List<Seckill> getSeckillList() {
		return seckillDao.queryAll(0, 4);
	}

	@Override
	public Seckill getById(long seckilled) {
		return seckillDao.queryById(seckilled);
	}

	@Override
	public Exposer exportSeckillUrl(long seckillId) {
		//在超时的基础上维护一致性
		//优化点:缓存优化
		//1.访问redis
		Seckill seckill = redisDao.getSeckill(seckillId);
		if (seckill == null) {
			//2.访问数据库
			seckill = seckillDao.queryById(seckillId);
			return new Exposer(false, seckillId);
		} else {
			//放入redis
			redisDao.putSeckill(seckill);
		}
		//使用redis缓存获取url的操作

		if (seckill == null) {
			return new Exposer(false, seckillId);
		}
		Date startTime = seckill.getStartTime();
		Date endTime = seckill.getEndTime();
		//系统当前时间
		Date nowTime = new Date();
		if (nowTime.getTime() < startTime.getTime() || nowTime.getTime() > endTime.getTime()) {
			return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
		}
		String md5 = getMD5(seckillId);//TODO
		return new Exposer(true, md5, seckillId);
	}

	private String getMD5(long seckillId) {
		String base = seckillId + "/" + slat;
		String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
		return md5;
	}

	@Override
	@Transactional
	/**使用注解控制事务的优点
	 * 1.开发团队达成一致约定,明确标注事务方法的编程风格.
	 * 2.保证事务方法的执行时间尽可能短.不要操作其他的网络操作,RPC/HTTP请求/或者剥离到事务方法外部.
	 * 3.不是所有的方法都需要事务,如只有一条修改操作,或者只读操作不需要事务
	 */

	public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillException {
		if (md5 == null || md5.equals(getMD5(seckillId))) {
			throw new SeckillException("seckill data rewrite");
		}
		try {
			//执行秒杀逻辑,减库存+记录购买行为
			Date nowTime = new Date();

			//--------------
			//改变update 和 insert 的顺序
			//并发访问
			int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
			//唯一:seckillId
			if (insertCount <= 0) {
				//重复秒杀
				throw new RepeatKillException("seckill repeated");
			} else {
				//更新数据库
				int updateCount = seckillDao.reduceNumber(seckillId, nowTime);
				if (updateCount <= 0) {
					//没有更新到记录
					throw new SeckillCloseException("seckill is closed");
				} else {
					//减库存成功,记录购买行为
					//成功秒杀
					SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
					return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
				}

			}

		} catch (SeckillCloseException e1) {
			throw e1;
		} catch (RepeatKillException e2) {
			throw e2;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			//所有编译器异常 转化为运行期异常
			throw new SeckillException("seckill inner erroe" + e.getMessage());
		}
	}

	/**
	 * 存储过程
	 *
	 * @param seckillId
	 * @param userPhone
	 * @param md5
	 * @return
	 * @throws SeckillException
	 * @throws RepeatKillException
	 * @throws SeckillException
	 */
	@Override
	public SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5) {
		if (md5 == null || !md5.equals(getMD5(seckillId))) {
			return new SeckillExecution(seckillId, SeckillStateEnum.DATA_REWRITE);
		}
		Date killTime = new Date();
		Map<String, Object> map = new HashMap<>();
		map.put("seckillId", seckillId);
		map.put("phone", userPhone);
		map.put("killTime", killTime);
		map.put("result", null);
		//执行存储过程,result复制
		try {
			seckillDao.killByProcedure(map);
			//获取之前的result
			int result = MapUtils.getInteger(map, "result", -2);
			if (result == 1) {
				SuccessKilled sk = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
				return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, sk);
			} else {
				return new SeckillExecution(seckillId, SeckillStateEnum.stateOf(result));
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return new SeckillExecution(seckillId, SeckillStateEnum.INNER_ERROR);
		}

	}
}
