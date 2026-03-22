package com.youth.hostel.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youth.hostel.entity.po.SysWallet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;

@Mapper
public interface SysWalletMapper extends BaseMapper<SysWallet> {

    @Update("UPDATE sys_wallet SET balance = balance - #{amount} WHERE user_id = #{userId} AND balance >= #{amount}")
    int deductBalance(@Param("userId") Long userId, @Param("amount") BigDecimal amount);
}
