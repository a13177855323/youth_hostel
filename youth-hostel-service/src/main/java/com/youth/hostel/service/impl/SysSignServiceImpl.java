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

        LambdaQueryWrapper<SysSignRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysSignRecord::getUserId, userId)
                .eq(SysSignRecord::getSignDate, today);
        if (baseMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("今日已签到");
        }

        int consecutiveDays = calculateConsecutiveDays(userId);

        int earnedPoints = calculateRewardPoints(consecutiveDays + 1);

        SysSignRecord signRecord = new SysSignRecord();
        signRecord.setUserId(userId);
        signRecord.setSignDate(today);
        signRecord.setRewardPoints(earnedPoints);
        baseMapper.insert(signRecord);

        SysUserPoints userPoints = userPointsMapper.selectOne(
                new LambdaQueryWrapper<SysUserPoints>().eq(SysUserPoints::getUserId, userId));
        if (userPoints == null) {
            userPoints = new SysUserPoints();
            userPoints.setUserId(userId);
            userPoints.setTotalPoints(earnedPoints);
            userPoints.setAvailablePoints(earnedPoints);
            userPointsMapper.insert(userPoints);
        } else {
            userPoints.setTotalPoints(userPoints.getTotalPoints() + earnedPoints);
            userPoints.setAvailablePoints(userPoints.getAvailablePoints() + earnedPoints);
            userPointsMapper.updateById(userPoints);
        }

        SignResultDTO result = new SignResultDTO();
        result.setSuccess(true);
        result.setPointsEarned(earnedPoints);
        result.setTotalPoints(userPoints.getTotalPoints());
        result.setConsecutiveDays(consecutiveDays + 1);
        return result;
    }

    @Override
    public SignResultDTO getSignInfo(Long userId, Integer year, Integer month) {
        if (year == null || month == null) {
            YearMonth currentMonth = YearMonth.now();
            year = currentMonth.getYear();
            month = currentMonth.getMonthValue();
        }

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        LambdaQueryWrapper<SysSignRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysSignRecord::getUserId, userId)
                .ge(SysSignRecord::getSignDate, startDate)
                .le(SysSignRecord::getSignDate, endDate)
                .orderByAsc(SysSignRecord::getSignDate);
        List<SysSignRecord> signRecords = baseMapper.selectList(wrapper);

        Map<Integer, Boolean> signCalendarMap = new HashMap<>();
        for (SysSignRecord record : signRecords) {
            signCalendarMap.put(record.getSignDate().getDayOfMonth(), true);
        }

        List<Map<String, Object>> signCalendar = new ArrayList<>();
        int daysInMonth = yearMonth.lengthOfMonth();
        for (int day = 1; day <= daysInMonth; day++) {
            Map<String, Object> dayInfo = new HashMap<>();
            dayInfo.put("day", day);
            dayInfo.put("signed", signCalendarMap.containsKey(day));
            signCalendar.add(dayInfo);
        }

        int consecutiveDays = calculateConsecutiveDays(userId);

        SysUserPoints userPoints = userPointsMapper.selectOne(
                new LambdaQueryWrapper<SysUserPoints>().eq(SysUserPoints::getUserId, userId));

        SignResultDTO result = new SignResultDTO();
        result.setSuccess(true);
        result.setConsecutiveDays(consecutiveDays);
        result.setTotalPoints(userPoints != null ? userPoints.getTotalPoints() : 0);
        result.setSignCalendar(signCalendar);
        return result;
    }

    private int calculateConsecutiveDays(Long userId) {
        LambdaQueryWrapper<SysSignRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysSignRecord::getUserId, userId)
                .orderByDesc(SysSignRecord::getSignDate)
                .last("LIMIT 1");
        SysSignRecord lastSign = baseMapper.selectOne(wrapper);

        if (lastSign == null) {
            return 0;
        }

        LocalDate today = LocalDate.now();
        LocalDate lastSignDate = lastSign.getSignDate();

        if (lastSignDate.equals(today.minusDays(1))) {
            return countConsecutiveDaysFrom(userId, lastSignDate);
        } else if (lastSignDate.equals(today)) {
            return countConsecutiveDaysFrom(userId, lastSignDate) - 1;
        } else {
            return 0;
        }
    }

    private int countConsecutiveDaysFrom(Long userId, LocalDate startDate) {
        int count = 0;
        LocalDate checkDate = startDate;

        while (true) {
            LambdaQueryWrapper<SysSignRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysSignRecord::getUserId, userId)
                    .eq(SysSignRecord::getSignDate, checkDate);
            if (baseMapper.selectCount(wrapper) > 0) {
                count++;
                checkDate = checkDate.minusDays(1);
            } else {
                break;
            }
        }

        return count;
    }

    private int calculateRewardPoints(int consecutiveDays) {
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
}
