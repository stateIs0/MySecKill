--数据库初始化脚本

-- 创建数据库
CREATE  database seckill;
-- 使用数据库
use seckill
--创建秒杀库存表
CREATE create table seckill (
  'seckill_id' bigint not null Auto_increment comment '商品库存id',
  'name' VARCHAR (120) not null comment '商品名称',
  'number' int NOT NULL  comment '库存数量',
  'start_time' TIMESTAMP  NOT  NULL comment '秒杀开启时间',
  'end_time' TIMESTAMP NOT NULL comment '秒杀结束时间',
  'create_time' TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
  PRIMARY KEY (secckill_id),
  KEY idx_start_time(start_time),
  KEY idx_end_time(end_time),
  KEY idx_create_time(create_time)
  -- 使用支持事务的引擎：innoDB default 表示默认使用utf-8
)ENGINE-InnoDB AUTO_Increment=1000 DEFAULT charset=utf-8 comment='秒杀库存表'

-- 初始化数据
INSERT INTO
  seckill(name, number, start_time,end_time)
VALUES
  ('1000元秒杀iphone7',100,'2017-09-08 00:00:00','2017-09-09 00:00:00'),
  ('500元秒杀ipad2',100,'2017-09-08 00:00:00','2017-09-09 00:00:00'),
  ('300元秒杀小米6',100,'2017-09-08 00:00:00','2017-09-09 00:00:00'),
  ('200元秒杀红米note',100,'2017-09-08 00:00:00','2017-09-09 00:00:00');

-- 秒杀成功明细表
-- 用户登录先关相关的信息
CREATE TABLE success_killed(
  'seckill_id' bigint NOT NULL comment '秒杀商品id',
  'user_phone' bigint NOT NULL comment '用户手机号',
  'state' tinyint NOT NULL DEFAULT 0 comment '状态表示：-1:无效，0成功，1：已付款',
  'create_time' TIMESTAMP NOT NULL comment '创建时间',
  PRIMARY KEY (seckil_id,user_phone),--联合主键
  KEY idx_create_time(create_time)
)ENGINE-InnoDB DEFAULT  charset=utf-8 comment='秒杀成功明细表'


--连接数据的控制台 密码是空 -p就可以了
--mysql -uroot -p

-- 为什么使用注释：使用：show create table seckill\G 命令可以看到mysql创建表的语句和注释。

-- 为什么手写sql
-- 记录每次上线的的DDL修改
