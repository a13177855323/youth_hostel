package com.youth.hostel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youth.hostel.common.exception.BusinessException;
import com.youth.hostel.dao.mapper.SysUserCheckinMapper;
import com.youth.hostel.dao.mapper.SysUserMapper;
import com.youth.hostel.entity.po.SysUser;
import com.youth.hostel.entity.po.SysUserCheckin;
import com.youth.hostel.entity.vo.CheckinInfoVO;
import com.youth.hostel.entity.vo.CheckinResultVO;
import com.youth.hostel.service.SysCheckinService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户签到服务实现
 */
@Service
@RequiredArgsConstructor
public class SysCheckinServiceImpl extends ServiceImpl<SysUserCheckinMapper, SysUserCheckin> implements SysCheckinService {

    private final SysUserCheckinMapper checkinMapper;
    private final SysUserMapper userMapper;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CheckinResultVO checkin(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        LocalDate today = LocalDate.now();
        CheckinResultVO result = new CheckinResultVO();

        // 1. 检查今日是否已签到
        SysUserCheckin todayCheckin = checkinMapper.selectByUserIdAndDate(userId, today);
        if (todayCheckin != null) {
            result.setSuccess(false);
            result.setMessage("今日已签到");
            result.setContinuousDays(todayCheckin.getContinuousDays());
            result.setPointsEarned(0);
            // 获取当前总积分
            SysUser user = userMapper.selectById(userId);
            result.setTotalPoints(user != null && user.getMemberPoints() != null ? user.getMemberPoints() : 0);
            return result;
        }

        // 2. 查询昨日签到记录，判断是否连续
        SysUserCheckin lastCheckin = checkinMapper.selectLastCheckin(userId);
        int continuousDays = 1; // 默认第一天

        if (lastCheckin != null) {
            LocalDate lastCheckinDate = lastCheckin.getCheckinDate();
            LocalDate yesterday = today.minusDays(1);
            // 如果昨日签到了，连续天数+1
            if (lastCheckinDate.equals(yesterday)) {
                continuousDays = lastCheckin.getContinuousDays() + 1;
            }
            // 如果昨日没签到但今日签到了，连续天数重置为1
            // 如果最后签到日期是今天之前且不是昨天，连续天数重置为1
        }

        // 3. 根据连续天数计算积分
        int pointsEarned = calculatePoints(continuousDays);

        // 4. 插入签到记录
        SysUserCheckin checkin = new SysUserCheckin();
        checkin.setUserId(userId);
        checkin.setCheckinDate(today);
        checkin.setContinuousDays(continuousDays);
        checkin.setPointsEarned(pointsEarned);
        checkinMapper.insert(checkin);

        // 5. 更新用户积分
        SysUser user = userMapper.selectById(userId);
        int totalPoints = pointsEarned;
        if (user != null) {
            totalPoints = (user.getMemberPoints() != null ? user.getMemberPoints() : 0) + pointsEarned;
            user.setMemberPoints(totalPoints);
            userMapper.updateById(user);
        }

        // 6. 返回结果
        result.setSuccess(true);
        result.setMessage("签到成功");
        result.setContinuousDays(continuousDays);
        result.setPointsEarned(pointsEarned);
        result.setTotalPoints(totalPoints);

        return result;
    }

    @Override
    public CheckinInfoVO getCheckinInfo(Long userId, Integer year, Integer month) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        CheckinInfoVO info = new CheckinInfoVO();

        // 1. 获取当前连续签到天数
        int continuousDays = getCurrentContinuousDays(userId);
        info.setContinuousDays(continuousDays);

        // 2. 获取用户积分
        SysUser user = userMapper.selectById(userId);
        int totalPoints = user != null && user.getMemberPoints() != null ? user.getMemberPoints() : 0;
        info.setTotalPoints(totalPoints);
        info.setAvailablePoints(totalPoints); // 可用积分暂时等于总积分

        // 3. 获取指定月份的签到日历
        YearMonth yearMonth;
        if (year == null || month == null) {
            yearMonth = YearMonth.now();
        } else {
            yearMonth = YearMonth.of(year, month);
        }

        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<SysUserCheckin> monthCheckins = checkinMapper.selectMonthCheckin(userId, startDate, endDate);

        // 构建签到日历Map
        Map<String, Boolean> calendar = new HashMap<>();
        List<LocalDate> checkinDates = monthCheckins.stream()
                .map(SysUserCheckin::getCheckinDate)
                .collect(Collectors.toList());

        // 初始化该月所有日期为未签到
        LocalDate date = startDate;
        while (!date.isAfter(endDate)) {
            calendar.put(date.format(DATE_FORMATTER), false);
            date = date.plusDays(1);
        }

        // 标记已签到日期
        for (LocalDate checkinDate : checkinDates) {
            calendar.put(checkinDate.format(DATE_FORMATTER), true);
        }

        info.setMonthCheckinCalendar(calendar);
        info.setMonthCheckinCount(checkinDates.size());
        info.setCheckinDates(checkinDates);

        return info;
    }

    @Override
    public Boolean isTodayCheckedIn(Long userId) {
        if (userId == null) {
            return false;
        }
        LocalDate today = LocalDate.now();
        SysUserCheckin todayCheckin = checkinMapper.selectByUserIdAndDate(userId, today);
        return todayCheckin != null;
    }

    @Override
    public Integer calculatePoints(Integer continuousDays) {
        if (continuousDays == null || continuousDays < 1) {
            return 0;
        }
        // 连续签到奖励规则
        // - 第1-3天：每天 10 积分
        // - 第4-7天：每天 20 积分
        // - 第8-30天：每天 30 积分
        // - 31天及以上：每天 50 积分
        if (continuousDays <= 3) {
            return 10;
        } else if (continuousDays <= 7) {
            return 20;
        } else if (continuousDays <= 30) {
            return 30;
        } else {
            return 50;
        }
    }

    /**
     * 获取用户当前连续签到天数
     */
    private int getCurrentContinuousDays(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDate checkDate = today;
        int continuousDays = 0;

        // 检查今日是否签到
        SysUserCheckin todayCheckin = checkinMapper.selectByUserIdAndDate(userId, today);
        if (todayCheckin != null) {
            continuousDays = 1;
            checkDate = today.minusDays(1);
        } else {
            // 从昨天开始检查
            checkDate = today.minusDays(1);
        }

        // 回溯检查连续签到
        while (true) {
            SysUserCheckin checkin = checkinMapper.selectByUserIdAndDate(userId, checkDate);
            if (checkin != null) {
                continuousDays++;
                checkDate = checkDate.minusDays(1);
            } else {
                break;
            }
        }

        return continuousDays;
    }
}
