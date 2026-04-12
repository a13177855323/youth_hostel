package com.youth.hostel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
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
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        LocalDate today = LocalDate.now();
        SignResultDTO result = new SignResultDTO();

        LambdaQueryWrapper<SysSignRecord> todayWrapper = new LambdaQueryWrapper<>();
        todayWrapper.eq(SysSignRecord::getUserId, userId)
                    .eq(SysSignRecord::getSignDate, today);
        SysSignRecord todaySign = getOne(todayWrapper);
        if (todaySign != null) {
            result.setSuccess(false);
            result.setPointsEarned(0);
            result.setConsecutiveDays(calculateConsecutiveDays(userId));
            result.setTotalPoints(getUserTotalPoints(userId));
            return result;
        }

        int consecutiveDays = calculateConsecutiveDays(userId) + 1;
        int rewardPoints = calculateRewardPoints(consecutiveDays);

        SysSignRecord signRecord = new SysSignRecord();
        signRecord.setUserId(userId);
        signRecord.setSignDate(today);
        signRecord.setRewardPoints(rewardPoints);
        save(signRecord);

        addUserPoints(userId, rewardPoints);

        result.setSuccess(true);
        result.setPointsEarned(rewardPoints);
        result.setConsecutiveDays(consecutiveDays);
        result.setTotalPoints(getUserTotalPoints(userId));

        return result;
    }

    @Override
    public SignResultDTO getSignInfo(Long userId, Integer year, Integer month) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        SignResultDTO result = new SignResultDTO();
        result.setSuccess(true);

        result.setConsecutiveDays(calculateConsecutiveDays(userId));
        result.setTotalPoints(getUserTotalPoints(userId));

        YearMonth yearMonth;
        if (year == null || month == null) {
            yearMonth = YearMonth.now();
        } else {
            yearMonth = YearMonth.of(year, month);
        }

        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        LambdaQueryWrapper<SysSignRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysSignRecord::getUserId, userId)
               .between(SysSignRecord::getSignDate, startDate, endDate);
        List<SysSignRecord> signRecords = list(wrapper);

        List<LocalDate> signedDates = new ArrayList<>();
        for (SysSignRecord record : signRecords) {
            signedDates.add(record.getSignDate());
        }

        List<Map<String, Object>> signCalendar = new ArrayList<>();
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            Map<String, Object> dayInfo = new HashMap<>();
            dayInfo.put("date", current.toString());
            dayInfo.put("signed", signedDates.contains(current));
            signCalendar.add(dayInfo);
            current = current.plusDays(1);
        }

        result.setSignCalendar(signCalendar);
        return result;
    }

    private int calculateConsecutiveDays(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDate checkDate = today.minusDays(1);
        int consecutiveDays = 0;

        LambdaQueryWrapper<SysSignRecord> todayWrapper = new LambdaQueryWrapper<>();
        todayWrapper.eq(SysSignRecord::getUserId, userId)
                    .eq(SysSignRecord::getSignDate, today);
        SysSignRecord todaySign = getOne(todayWrapper);
        if (todaySign != null) {
            consecutiveDays = 1;
        }

        while (true) {
            LambdaQueryWrapper<SysSignRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysSignRecord::getUserId, userId)
                   .eq(SysSignRecord::getSignDate, checkDate);
            SysSignRecord record = getOne(wrapper);
            if (record != null) {
                consecutiveDays++;
                checkDate = checkDate.minusDays(1);
            } else {
                break;
            }
        }

        return consecutiveDays;
    }

    private int calculateRewardPoints(int consecutiveDays) {
        if (consecutiveDays <= 3) {
            return 10;
        } else if (consecutiveDays <= 7) {
            return 20;
        } else if (consecutiveDays <= 30) {
            return 30;
        } else {
            return 50;
        }
    }

    private Integer getUserTotalPoints(Long userId) {
        LambdaQueryWrapper<SysUserPoints> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserPoints::getUserId, userId);
        SysUserPoints userPoints = userPointsMapper.selectOne(wrapper);
        return userPoints != null && userPoints.getTotalPoints() != null ? userPoints.getTotalPoints() : 0;
    }

    private void addUserPoints(Long userId, int points) {
        LambdaQueryWrapper<SysUserPoints> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserPoints::getUserId, userId);
        SysUserPoints userPoints = userPointsMapper.selectOne(wrapper);

        if (userPoints == null) {
            userPoints = new SysUserPoints();
            userPoints.setUserId(userId);
            userPoints.setTotalPoints(points);
            userPoints.setAvailablePoints(points);
            userPointsMapper.insert(userPoints);
        } else {
            LambdaUpdateWrapper<SysUserPoints> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(SysUserPoints::getUserId, userId)
                         .setSql("total_points = total_points + " + points)
                         .setSql("available_points = available_points + " + points);
            userPointsMapper.update(null, updateWrapper);
        }
    }
}
