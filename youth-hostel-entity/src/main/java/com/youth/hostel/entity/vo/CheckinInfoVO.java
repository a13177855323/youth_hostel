package com.youth.hostel.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 签到信息VO
 */
@Data
public class CheckinInfoVO implements Serializable {

    /**
     * 当前连续签到天数
     */
    private Integer continuousDays;

    /**
     * 总积分
     */
    private Integer totalPoints;

    /**
     * 可用积分（总积分 - 已使用积分，这里简化为总积分）
     */
    private Integer availablePoints;

    /**
     * 指定月份的签到日历
     * key: 日期字符串（格式：yyyy-MM-dd）
     * value: 是否签到（true-已签到，false-未签到）
     */
    private Map<String, Boolean> monthCheckinCalendar;

    /**
     * 当月已签到天数
     */
    private Integer monthCheckinCount;

    /**
     * 本月签到的日期列表
     */
    private List<LocalDate> checkinDates;
}
