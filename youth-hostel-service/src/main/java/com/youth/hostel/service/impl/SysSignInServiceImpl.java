package com.youth.hostel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youth.hostel.common.exception.BusinessException;
import com.youth.hostel.dao.mapper.SysSignInMapper;
import com.youth.hostel.dao.mapper.SysUserPointsMapper;
import com.youth.hostel.entity.po.SysSignIn;
import com.youth.hostel.entity.po.SysUserPoints;
import com.youth.hostel.entity.vo.SignInInfoVO;
import com.youth.hostel.entity.vo.SignInResultVO;
import com.youth.hostel.service.SysSignInService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SysSignInServiceImpl extends ServiceImpl<SysSignInMapper, SysSignIn> implements SysSignInService {

    private final SysUserPointsMapper userPointsMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SignInResultVO signIn(Long userId) {
        LocalDate today = LocalDate.now();

        LambdaQueryWrapper<SysSignIn> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysSignIn::getUserId, userId)
                .eq(SysSignIn::getSignInDate, today);
        if (baseMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("今日已签到");
        }

        int continuousDays = calculateContinuousDays(userId);

        int earnedPoints = calculatePoints(continuousDays + 1);

        SysSignIn signIn = new SysSignIn();
        signIn.setUserId(userId);
        signIn.setSignInDate(today);
        signIn.setContinuousDays(continuousDays + 1);
        signIn.setPoints(earnedPoints);
        baseMapper.insert(signIn);

        SysUserPoints userPoints = userPointsMapper.selectOne(
                new LambdaQueryWrapper<SysUserPoints>().eq(SysUserPoints::getUserId, userId));
        if (userPoints == null) {
            userPoints = new SysUserPoints();
            userPoints.setUserId(userId);
            userPoints.setTotalPoints(earnedPoints);
            userPoints.setAvailablePoints(earnedPoints);
            userPointsMapper.insert(userPoints);
        } else {
            userPointsMapper.addPoints(userId, earnedPoints);
            userPoints.setTotalPoints(userPoints.getTotalPoints() + earnedPoints);
            userPoints.setAvailablePoints(userPoints.getAvailablePoints() + earnedPoints);
        }

        SignInResultVO result = new SignInResultVO();
        result.setEarnedPoints(earnedPoints);
        result.setTotalPoints(userPoints.getTotalPoints());
        result.setContinuousDays(continuousDays + 1);
        return result;
    }

    @Override
    public SignInInfoVO getSignInInfo(Long userId, YearMonth yearMonth) {
        if (yearMonth == null) {
            yearMonth = YearMonth.now();
        }

        LambdaQueryWrapper<SysSignIn> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysSignIn::getUserId, userId)
                .ge(SysSignIn::getSignInDate, yearMonth.atDay(1))
                .lt(SysSignIn::getSignInDate, yearMonth.plusMonths(1).atDay(1))
                .orderByAsc(SysSignIn::getSignInDate);
        List<SysSignIn> signInList = baseMapper.selectList(wrapper);

        int continuousDays = calculateContinuousDays(userId);

        SysUserPoints userPoints = userPointsMapper.selectOne(
                new LambdaQueryWrapper<SysUserPoints>().eq(SysUserPoints::getUserId, userId));

        SignInInfoVO info = new SignInInfoVO();
        info.setContinuousDays(continuousDays);
        info.setTotalPoints(userPoints != null ? userPoints.getTotalPoints() : 0);
        info.setAvailablePoints(userPoints != null ? userPoints.getAvailablePoints() : 0);
        info.setSignInDates(signInList.stream().map(SysSignIn::getSignInDate).toList());
        return info;
    }

    private int calculateContinuousDays(Long userId) {
        LambdaQueryWrapper<SysSignIn> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysSignIn::getUserId, userId)
                .orderByDesc(SysSignIn::getSignInDate)
                .last("LIMIT 1");
        SysSignIn lastSignIn = baseMapper.selectOne(wrapper);

        if (lastSignIn == null) {
            return 0;
        }

        LocalDate today = LocalDate.now();
        LocalDate lastDate = lastSignIn.getSignInDate();

        if (lastDate.equals(today.minusDays(1))) {
            return lastSignIn.getContinuousDays();
        } else if (lastDate.equals(today)) {
            return lastSignIn.getContinuousDays() - 1;
        } else {
            return 0;
        }
    }

    private int calculatePoints(int continuousDays) {
        if (continuousDays >= 31) {
            return 50;
        } else if (continuousDays >= 8) {
            return 30;
        } else if (continuousDays >= 4) {
            return 20;
        } else {
            return 10;
        }
    }
}
