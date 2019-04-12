-- 数据库初始化脚本

-- 创建数据库
CREATE DATABASE seckill;

-- 使用数据库
use seckill;

-- 创建秒杀库存表
CREATE TABLE seckill(
    `seckill_id` bigint NOT NUll AUTO_INCREMENT COMMENT '商品库存id',
    `name` varchar(80) NOT NULL  COMMENT '商品名称',
    `number` int NOT NULL COMMENT '商品库存',
    `start_time` timestamp NOT NULL COMMENT '秒杀开启时间',
    `end_time` timestamp NOT NULL COMMENT '秒杀结束时间',
    `create_time` timestamp NOT NUll DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (seckill_id),
    key idx_start_time(start_time),
    key idx_end_time(end_time),
    key idx_create_time(create_time)
)ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COMMENT='秒杀库存表';

-- 初始化数据

insert into
    seckill(name,number,start_time,end_time)
values
    ('1000秒杀iphone6',100,'2015-11-01 00:00:00','2015-11-02 00:00:00'),
    ('500秒杀ipho2',200,'2015-11-01 00:00:00','2015-11-02 00:00:00'),
    ('400秒杀小米4',300,'2015-11-01 00:00:00','2015-11-02 00:00:00'),
    ('200秒杀红米',100,'2015-11-01 00:00:00','2015-11-02 00:00:00');



-- 秒杀成功明细表



-- 用户登录认证相关信息
create  table success_killed(
    `seckill_id` bigint NOT NULL COMMENT '秒杀商品id',
    `user_phone` bigint NOT NULL COMMENT '用户手机号',
    `start` tinyint NOT NUll DEFAULT -1 COMMENT '状态标示:-1:无效 0:成功 1:已付款',
    `create_time` timestamp NOT NULL COMMENT '创建时间',
    PRIMARY KEY (seckill_id,user_phone),/*联合主键*/
    key idx_create_time(create_time)
)ENGINE=InnoDB DEFAULT CHARSET=UTF8 COMMENT='秒杀成功明细表';



-- 连接数据库控制台

mysql -uroot -p