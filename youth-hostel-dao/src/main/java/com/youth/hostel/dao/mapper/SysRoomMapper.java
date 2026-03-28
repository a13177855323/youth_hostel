package com.youth.hostel.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youth.hostel.entity.po.SysRoom;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SysRoomMapper extends BaseMapper<SysRoom> {

    /**
     * 原子扣减库存（解决并发超卖问题）
     * 利用数据库UPDATE的原子性，在SQL层面同时做库存检查和扣减
     */
    @Update("UPDATE sys_room SET stock = stock - #{quantity} WHERE id = #{roomId} AND stock >= #{quantity}")
    int decreaseStock(@Param("roomId") Long roomId, @Param("quantity") Integer quantity);
}
