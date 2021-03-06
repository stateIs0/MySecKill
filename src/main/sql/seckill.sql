-- 秒杀执行的存储过程
--；
DELIMITER $$ -- 将原本是分号的换行符改成$$ ,防止存储过程和mysql冲突。
-- 定义存储过程
-- 参数，in表示输入参数，out表示输出参数
--row_count():返回上一条修改类型sql（delete ,insert, update）的影响行数
--row_count():0:未修改数据，>0 表示修改的行数，<0 :sql错误/未执行修改sql
CREATE PROCEDURE 'seckill'.'execute_seckill'
  (in v_seckill-id bigint,in v_phone bigint,in v_kill_time TIMESTAMP ,out r_result int)
  BEGIN
    DECLARE insert_count int DEFAULT 0;
    start TRANSACTION;
    insert igonre into success_killid
    (seckill_id, user_phone, create_time)
    VALUES
    (v_seckill-id, v_phone, v_kill_time);
    SELECT ROW_COUNT INTO insert_into;
    if (insert_into = 0) THEN
      ROLLBACK ;
      set r_result = -1;
    ELSE IF (insert_into < 0)THEN
      ROLLBACK ;
      set r_result = -2;
    ELSE
      udpate seckill
      set number = number -1
      where seckill_id = v_seckill_id
      and end_time > v_kill_time
      and start_time < v_kill_time
      and number > 0;
      SELECT ROW_COUNT INTO insert_into;
      IF (insert_into = 0) THEN
        ROLLBACK ;
        set r_result = 0;
      ELSE IF (insert_into < 0 )THEN
        ROLLBACK ;
        set r_result = -2;
      ELSE
       COMMIT ;
       set r_result = 1;
      END IF;
    END IF;
  END;
$$
-- 存储过程定义结束


DELIMITER $$
set @r_result = -3;
-- 执行存储过程
call execute_seckill(1003, 13233232322, now(), @r_result);

select @r_result;

-- 存储过程优化：事务行级锁持有的时间
-- 不要过度依赖存储过程
-- 简单的逻辑可以应用存储过程
-- 这样的存储过程QPS：一个秒杀单6000左右的QPS。
