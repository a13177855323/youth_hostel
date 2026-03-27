package com.youth.hostel.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.youth.hostel.entity.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 用户签到记录表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user_checkin")
public class SysUserCheckin extends BaseEntity {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 签到日期
     */
    private LocalDate checkinDate;

    /**
     * 连续签到天数
     */
    private Integer continuousDays;

    /**
     * 本次获得积分
     */
    private Integer pointsEarned;
}
