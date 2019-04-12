package com.sparrow.zg.dao;

import com.sparrow.zg.entity.Seckill;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface SeckillDao {
	/**
	 * 减库存
	 * @param seckillId
	 * @param killTime
	 * @return
	 */
	int reduceNumber(@Param("seckillId") long seckillId, @Param("killTime") Date killTime);

	Seckill queryById(long seckillId);

	List<Seckill> queryAll(@Param("offet") int offet, @Param("limit") int limit);
}
