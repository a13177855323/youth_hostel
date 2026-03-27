package com.youth.hostel.service;

import com.youth.hostel.entity.vo.CheckinInfoVO;
import com.youth.hostel.entity.vo.CheckinResultVO;

/**
 * 用户签到服务接口
 */
public interface SysCheckinService {

    /**
     * 用户签到
     *
     * @param userId 用户ID
     * @return 签到结果
     */
    CheckinResultVO checkin(Long userId);

    /**
     * 获取用户签到信息
     *
     * @param userId 用户ID
     * @param year   年份（null表示当前年）
     * @param month  月份（null表示当前月）
     * @return 签到信息
     */
    CheckinInfoVO getCheckinInfo(Long userId, Integer year, Integer month);

    /**
     * 判断用户今日是否已签到
     *
     * @param userId 用户ID
     * @return true-已签到，false-未签到
     */
    Boolean isTodayCheckedIn(Long userId);

    /**
     * 根据连续签到天数计算应获得的积分
     *
     * @param continuousDays 连续签到天数
     * @return 应获得的积分
     */
    Integer calculatePoints(Integer continuousDays);
}
