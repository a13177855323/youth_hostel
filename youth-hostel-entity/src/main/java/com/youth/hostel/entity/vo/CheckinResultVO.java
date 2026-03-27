package com.youth.hostel.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 签到结果VO
 */
@Data
public class CheckinResultVO implements Serializable {

    /**
     * 是否签到成功
     */
    private Boolean success;

    /**
     * 连续签到天数
     */
    private Integer continuousDays;

    /**
     * 本次获得积分
     */
    private Integer pointsEarned;

    /**
     * 当前总积分
     */
    private Integer totalPoints;

    /**
     * 消息（如"今日已签到"等）
     */
    private String message;
}
