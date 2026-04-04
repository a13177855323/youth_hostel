package com.youth.hostel.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youth.hostel.entity.po.SysRoom;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SysRoomMapper extends BaseMapper<SysRoom> {

    @Update("UPDATE sys_room SET stock = stock - #{quantity} WHERE id = #{roomId} AND stock >= #{quantity}")
    int deductStock(@Param("roomId") Long roomId, @Param("quantity") Integer quantity);
}
