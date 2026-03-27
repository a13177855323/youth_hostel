package com.youth.hostel.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youth.hostel.entity.po.SysUserPoints;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SysUserPointsMapper extends BaseMapper<SysUserPoints> {

    @Update("UPDATE sys_user_points SET total_points = total_points + #{points}, available_points = available_points + #{points} WHERE user_id = #{userId}")
    int addPoints(@Param("userId") Long userId, @Param("points") Integer points);
}
