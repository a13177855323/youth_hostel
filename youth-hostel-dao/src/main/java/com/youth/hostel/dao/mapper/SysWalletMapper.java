package com.youth.hostel.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youth.hostel.entity.po.SysWallet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;

@Mapper
public interface SysWalletMapper extends BaseMapper<SysWallet> {

    /**
     * 原子扣减钱包余额（解决并发超扣问题）
     * 通过数据库行锁保证并发安全，同时检查余额充足
     * @return 影响行数：1-扣减成功，0-余额不足或钱包不存在
     */
    @Update("UPDATE sys_wallet SET balance = balance - #{amount} " +
            "WHERE user_id = #{userId} AND balance >= #{amount}")
    int decreaseBalance(@Param("userId") Long userId, @Param("amount") BigDecimal amount);
}
