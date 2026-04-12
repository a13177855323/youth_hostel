package com.youth.hostel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youth.hostel.common.exception.BusinessException;
import com.youth.hostel.dao.mapper.SysSignRecordMapper;
import com.youth.hostel.dao.mapper.SysUserPointsMapper;
import com.youth.hostel.entity.dto.SignResultDTO;
import com.youth.hostel.entity.po.SysSignRecord;
import com.youth.hostel.entity.po.SysUserPoints;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SysSignServiceImplTest {

    @Mock
    private SysSignRecordMapper signRecordMapper;

    @Mock
    private SysUserPointsMapper userPointsMapper;

    @InjectMocks
    private SysSignServiceImpl signService;

    private Long testUserId;

    @BeforeEach
    void setUp() {
        testUserId = 1L;
    }

    @Test
    @DisplayName("首次签到 - 应该获得10积分")
    void testFirstSignIn() {
        when(signRecordMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(signRecordMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(signRecordMapper.insert(any(SysSignRecord.class))).thenReturn(1);
        when(userPointsMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(userPointsMapper.insert(any(SysUserPoints.class))).thenReturn(1);

        SignResultDTO result = signService.sign(testUserId);

        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals(10, result.getPointsEarned());
        assertEquals(10, result.getTotalPoints());
        assertEquals(1, result.getConsecutiveDays());

        verify(signRecordMapper).insert(any(SysSignRecord.class));
        verify(userPointsMapper).insert(any(SysUserPoints.class));
    }

    @Test
    @DisplayName("重复签到 - 应该抛出异常")
    void testDuplicateSignIn() {
        when(signRecordMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

        assertThrows(BusinessException.class, () -> signService.sign(testUserId));

        verify(signRecordMapper, never()).insert(any(SysSignRecord.class));
        verify(userPointsMapper, never()).insert(any(SysUserPoints.class));
        verify(userPointsMapper, never()).updateById(any(SysUserPoints.class));
    }

    @Test
    @DisplayName("连续签到第4天 - 应该获得20积分")
    void testConsecutiveDay4() {
        SysSignRecord lastSign = new SysSignRecord();
        lastSign.setUserId(testUserId);
        lastSign.setSignDate(LocalDate.now().minusDays(1));
        lastSign.setRewardPoints(10);

        when(signRecordMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(signRecordMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(lastSign);
        when(signRecordMapper.insert(any(SysSignRecord.class))).thenReturn(1);

        SysUserPoints existingPoints = new SysUserPoints();
        existingPoints.setUserId(testUserId);
        existingPoints.setTotalPoints(30);
        existingPoints.setAvailablePoints(30);
        when(userPointsMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(existingPoints);
        when(userPointsMapper.updateById(any(SysUserPoints.class))).thenReturn(1);

        when(signRecordMapper.selectCount(argThat(wrapper -> {
            return true;
        }))).thenAnswer(invocation -> {
            return 1L;
        });

        SignResultDTO result = signService.sign(testUserId);

        assertNotNull(result);
        assertEquals(20, result.getPointsEarned());
        assertEquals(50, result.getTotalPoints());
    }

    @Test
    @DisplayName("断签后重新签到 - 连续天数应重置为1")
    void testBrokenStreak() {
        SysSignRecord oldSign = new SysSignRecord();
        oldSign.setUserId(testUserId);
        oldSign.setSignDate(LocalDate.now().minusDays(3));
        oldSign.setRewardPoints(10);

        when(signRecordMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(signRecordMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(oldSign);
        when(signRecordMapper.insert(any(SysSignRecord.class))).thenReturn(1);

        SysUserPoints existingPoints = new SysUserPoints();
        existingPoints.setUserId(testUserId);
        existingPoints.setTotalPoints(30);
        existingPoints.setAvailablePoints(30);
        when(userPointsMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(existingPoints);
        when(userPointsMapper.updateById(any(SysUserPoints.class))).thenReturn(1);

        SignResultDTO result = signService.sign(testUserId);

        assertNotNull(result);
        assertEquals(1, result.getConsecutiveDays());
        assertEquals(10, result.getPointsEarned());
    }

    @Test
    @DisplayName("获取签到信息 - 返回月度日历")
    void testGetSignInfo() {
        SysSignRecord record1 = new SysSignRecord();
        record1.setUserId(testUserId);
        record1.setSignDate(LocalDate.now().withDayOfMonth(1));
        record1.setRewardPoints(10);

        SysSignRecord record2 = new SysSignRecord();
        record2.setUserId(testUserId);
        record2.setSignDate(LocalDate.now().withDayOfMonth(2));
        record2.setRewardPoints(10);

        when(signRecordMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(List.of(record1, record2));
        when(signRecordMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(record2);

        SysUserPoints userPoints = new SysUserPoints();
        userPoints.setUserId(testUserId);
        userPoints.setTotalPoints(20);
        userPoints.setAvailablePoints(20);
        when(userPointsMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(userPoints);

        SignResultDTO result = signService.getSignInfo(testUserId, null, null);

        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertNotNull(result.getSignCalendar());
        assertEquals(20, result.getTotalPoints());
    }

    @Test
    @DisplayName("获取签到信息 - 用户无积分记录")
    void testGetSignInfoNoPoints() {
        when(signRecordMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.emptyList());
        when(signRecordMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(userPointsMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        SignResultDTO result = signService.getSignInfo(testUserId, 2024, 1);

        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals(0, result.getTotalPoints());
        assertEquals(0, result.getConsecutiveDays());
        assertNotNull(result.getSignCalendar());
    }

    @Test
    @DisplayName("积分规则验证 - 第1天10积分")
    void testPointsRuleDay1() {
        when(signRecordMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(signRecordMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(signRecordMapper.insert(any(SysSignRecord.class))).thenReturn(1);
        when(userPointsMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(userPointsMapper.insert(any(SysUserPoints.class))).thenReturn(1);

        SignResultDTO result = signService.sign(testUserId);

        assertEquals(10, result.getPointsEarned());
    }

    @Test
    @DisplayName("积分规则验证 - 第31天50积分")
    void testPointsRuleDay31() {
        SysSignRecord lastSign = new SysSignRecord();
        lastSign.setUserId(testUserId);
        lastSign.setSignDate(LocalDate.now().minusDays(1));
        lastSign.setRewardPoints(30);

        when(signRecordMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(signRecordMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(lastSign);
        when(signRecordMapper.insert(any(SysSignRecord.class))).thenReturn(1);

        SysUserPoints existingPoints = new SysUserPoints();
        existingPoints.setUserId(testUserId);
        existingPoints.setTotalPoints(900);
        existingPoints.setAvailablePoints(900);
        when(userPointsMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(existingPoints);
        when(userPointsMapper.updateById(any(SysUserPoints.class))).thenReturn(1);

        when(signRecordMapper.selectCount(argThat(wrapper -> true))).thenAnswer(invocation -> 31L);

        SignResultDTO result = signService.sign(testUserId);

        assertEquals(50, result.getPointsEarned());
    }
}
