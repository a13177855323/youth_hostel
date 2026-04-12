package com.youth.hostel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youth.hostel.common.exception.BusinessException;
import com.youth.hostel.dao.mapper.SysSignRecordMapper;
import com.youth.hostel.dao.mapper.SysUserPointsMapper;
import com.youth.hostel.entity.dto.SignResultDTO;
import com.youth.hostel.entity.po.SysSignRecord;
import com.youth.hostel.entity.po.SysUserPoints;
import com.youth.hostel.service.SysSignService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SysSignServiceImpl extends ServiceImpl<SysSignRecordMapper, SysSignRecord> implements SysSignService {

    private final SysUserPointsMapper userPointsMapper;

    public SysSignServiceImpl(SysUserPointsMapper userPointsMapper) {
        this.userPointsMapper = userPointsMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SignResultDTO sign(Long userId) {
        LocalDate today = LocalDate.now();

        // 检查今天是否已经签到
        Long count = baseMapper.selectCount(new LambdaQueryWrapper<SysSignRecord>()
                .eq(SysSignRecord::getUserId, userId)
                .eq(SysSignRecord::getSignDate, today));
        if (count > 0) {
            throw new BusinessException("今日已签到，请勿重复签到");
        }

        // 计算签到前的连续签到天数（用于判断断签情况）
        int consecutiveDaysBefore = calculateConsecutiveDaysBeforeToday(userId);

        // 本次签到后的连续天数 = 之前的连续天数 + 1
        int consecutiveDaysAfter = consecutiveDaysBefore + 1;

        // 计算本次签到获得的积分（基于签到后的连续天数）
        int pointsEarned = calculatePoints(consecutiveDaysAfter);

        // 保存签到记录
        SysSignRecord signRecord = new SysSignRecord();
        signRecord.setUserId(userId);
        signRecord.setSignDate(today);
        signRecord.setRewardPoints(pointsEarned);
        baseMapper.insert(signRecord);

        // 更新用户积分
        SysUserPoints userPoints = getOrCreateUserPoints(userId);
        userPoints.setTotalPoints(userPoints.getTotalPoints() + pointsEarned);
        userPoints.setAvailablePoints(userPoints.getAvailablePoints() + pointsEarned);
        userPointsMapper.updateById(userPoints);

        // 构建返回结果
        SignResultDTO result = new SignResultDTO();
        result.setSuccess(true);
        result.setPointsEarned(pointsEarned);
        result.setTotalPoints(userPoints.getTotalPoints());
        result.setConsecutiveDays(consecutiveDaysAfter);

        return result;
    }

    @Override
    public SignResultDTO getSignInfo(Long userId, Integer year, Integer month) {
        // 获取用户积分信息
        SysUserPoints userPoints = getOrCreateUserPoints(userId);

        // 计算连续签到天数
        int consecutiveDays = calculateConsecutiveDays(userId);

        // 生成签到日历
        List<Map<String, Object>> signCalendar = generateSignCalendar(userId, year, month);

        // 构建返回结果
        SignResultDTO result = new SignResultDTO();
        result.setSuccess(true);
        result.setTotalPoints(userPoints.getTotalPoints());
        result.setConsecutiveDays(consecutiveDays);
        result.setSignCalendar(signCalendar);

        return result;
    }

    /**
     * 计算今天之前的连续签到天数（不包含今天）
     * 用于判断本次签到应该获得多少积分
     */
    private int calculateConsecutiveDaysBeforeToday(Long userId) {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        int consecutiveDays = 0;
        LocalDate checkDate = yesterday;

        // 从昨天开始往前计算连续签到天数
        while (true) {
            Long count = baseMapper.selectCount(new LambdaQueryWrapper<SysSignRecord>()
                    .eq(SysSignRecord::getUserId, userId)
                    .eq(SysSignRecord::getSignDate, checkDate));
            if (count > 0) {
                consecutiveDays++;
                checkDate = checkDate.minusDays(1);
            } else {
                break;
            }
        }

        return consecutiveDays;
    }

    /**
     * 计算当前连续签到天数（包含今天，如果今天已签到）
     * 用于获取签到信息
     */
    private int calculateConsecutiveDays(Long userId) {
        LocalDate today = LocalDate.now();
        int consecutiveDays = calculateConsecutiveDaysBeforeToday(userId);

        // 检查今天是否已签到
        Long todayCount = baseMapper.selectCount(new LambdaQueryWrapper<SysSignRecord>()
                .eq(SysSignRecord::getUserId, userId)
                .eq(SysSignRecord::getSignDate, today));

        if (todayCount > 0) {
            consecutiveDays++;
        } else if (consecutiveDays == 0) {
            // 今天没签到且昨天也没签到，返回0
            return 0;
        }

        return consecutiveDays;
    }

    /**
     * 根据连续签到天数计算积分
     */
    private int calculatePoints(int consecutiveDays) {
        if (consecutiveDays >= 31) {
            return 50;
        } else if (consecutiveDays >= 8) {
            return 30;
        } else if (consecutiveDays >= 4) {
            return 20;
        } else {
            return 10;
        }
    }

    /**
     * 获取或创建用户积分记录
     */
    private SysUserPoints getOrCreateUserPoints(Long userId) {
        LambdaQueryWrapper<SysUserPoints> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserPoints::getUserId, userId);
        SysUserPoints userPoints = userPointsMapper.selectOne(wrapper);

        if (userPoints == null) {
            userPoints = new SysUserPoints();
            userPoints.setUserId(userId);
            userPoints.setTotalPoints(0);
            userPoints.setAvailablePoints(0);
            userPointsMapper.insert(userPoints);
        }

        return userPoints;
    }

    /**
     * 生成签到日历
     */
    private List<Map<String, Object>> generateSignCalendar(Long userId, Integer year, Integer month) {
        List<Map<String, Object>> calendar = new ArrayList<>();

        YearMonth yearMonth = YearMonth.of(year, month);
        int daysInMonth = yearMonth.lengthOfMonth();

        // 查询该月所有签到记录
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = LocalDate.of(year, month, daysInMonth);

        List<SysSignRecord> signRecords = baseMapper.selectList(new LambdaQueryWrapper<SysSignRecord>()
                .eq(SysSignRecord::getUserId, userId)
                .ge(SysSignRecord::getSignDate, startDate)
                .le(SysSignRecord::getSignDate, endDate));

        // 将签到记录转换为Map，方便查询
        Map<Integer, Boolean> signMap = new HashMap<>();
        for (SysSignRecord record : signRecords) {
            signMap.put(record.getSignDate().getDayOfMonth(), true);
        }

        // 生成日历
        for (int day = 1; day <= daysInMonth; day++) {
            Map<String, Object> dayInfo = new HashMap<>();
            dayInfo.put("day", day);
            dayInfo.put("signed", signMap.getOrDefault(day, false));
            calendar.add(dayInfo);
        }

        return calendar;
    }
}
