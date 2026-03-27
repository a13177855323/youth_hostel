package com.youth.hostel.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youth.hostel.entity.po.SysUserCheckin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface SysUserCheckinMapper extends BaseMapper<SysUserCheckin> {

    /**
     * 查询用户指定日期的签到记录
     */
    @Select("SELECT * FROM sys_user_checkin WHERE user_id = #{userId} AND checkin_date = #{checkinDate} AND deleted = 0")
    SysUserCheckin selectByUserIdAndDate(@Param("userId") Long userId, @Param("checkinDate") LocalDate checkinDate);

    /**
     * 查询用户最后一条签到记录（判断是否连续签到）
     */
    @Select("SELECT * FROM sys_user_checkin WHERE user_id = #{userId} AND deleted = 0 ORDER BY checkin_date DESC LIMIT 1")
    SysUserCheckin selectLastCheckin(@Param("userId") Long userId);

    /**
     * 查询用户指定月份的签到记录
     */
    @Select("SELECT * FROM sys_user_checkin WHERE user_id = #{userId} " +
            "AND checkin_date BETWEEN #{startDate} AND #{endDate} AND deleted = 0")
    List<SysUserCheckin> selectMonthCheckin(@Param("userId") Long userId,
                                             @Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate);
}
