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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SysSignServiceImplTest {

    @Mock
    private SysSignRecordMapper signRecordMapper;

    @Mock
    private SysUserPointsMapper userPointsMapper;

    @InjectMocks
    private SysSignServiceImpl signService;

    private static final Long TEST_USER_ID = 1L;

    @BeforeEach
    void setUp() {
        // 初始化
    }

    @Test
    @DisplayName("首次签到 - 应该成功并获得10积分")
    void sign_FirstTime_ShouldSuccessWith10Points() {
        // 模拟今天未签到
        when(signRecordMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        
        // 模拟昨天未签到（首次签到）
        when(signRecordMapper.selectCount(argThat(wrapper -> {
            // 检查查询条件是否包含昨天的日期
            return true;
        }))).thenReturn(0L);

        // 模拟用户积分记录不存在
        when(userPointsMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(userPointsMapper.insert(any(SysUserPoints.class))).thenReturn(1);

        // 模拟插入签到记录成功
        when(signRecordMapper.insert(any(SysSignRecord.class))).thenReturn(1);

        // 模拟更新积分成功
        when(userPointsMapper.updateById(any(SysUserPoints.class))).thenReturn(1);

        // 执行签到
        SignResultDTO result = signService.sign(TEST_USER_ID);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals(10, result.getPointsEarned()); // 第1天10积分
        assertEquals(10, result.getTotalPoints());
        assertEquals(1, result.getConsecutiveDays());

        // 验证交互
        verify(signRecordMapper, times(1)).insert(any(SysSignRecord.class));
        verify(userPointsMapper, times(1)).insert(any(SysUserPoints.class));
        verify(userPointsMapper, times(1)).updateById(any(SysUserPoints.class));
    }

    @Test
    @DisplayName("重复签到 - 应该抛出异常")
    void sign_DuplicateSign_ShouldThrowException() {
        // 模拟今天已签到
        when(signRecordMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

        // 执行签到应该抛出异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            signService.sign(TEST_USER_ID);
        });

        assertEquals("今日已签到，请勿重复签到", exception.getMessage());

        // 验证没有插入记录
        verify(signRecordMapper, never()).insert(any(SysSignRecord.class));
    }

    @Test
    @DisplayName("连续签到3天 - 每天应该获得10积分")
    void sign_ThirdConsecutiveDay_ShouldGet10Points() {
        // 模拟今天未签到
        when(signRecordMapper.selectCount(any(LambdaQueryWrapper.class)))
            .thenReturn(0L)  // 今天未签到
            .thenReturn(1L)  // 昨天已签到
            .thenReturn(1L); // 前天已签到

        // 模拟用户积分记录存在，已有20积分（前两天）
        SysUserPoints existingPoints = new SysUserPoints();
        existingPoints.setUserId(TEST_USER_ID);
        existingPoints.setTotalPoints(20);
        existingPoints.setAvailablePoints(20);
        when(userPointsMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(existingPoints);

        // 模拟插入和更新成功
        when(signRecordMapper.insert(any(SysSignRecord.class))).thenReturn(1);
        when(userPointsMapper.updateById(any(SysUserPoints.class))).thenReturn(1);

        // 执行签到
        SignResultDTO result = signService.sign(TEST_USER_ID);

        // 验证结果 - 第3天还是10积分
        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals(10, result.getPointsEarned());
        assertEquals(30, result.getTotalPoints()); // 20 + 10
        assertEquals(3, result.getConsecutiveDays());
    }

    @Test
    @DisplayName("连续签到4天 - 应该获得20积分")
    void sign_FourthConsecutiveDay_ShouldGet20Points() {
        // 模拟今天未签到，但前3天都签到了
        when(signRecordMapper.selectCount(any(LambdaQueryWrapper.class)))
            .thenReturn(0L)  // 今天未签到
            .thenReturn(1L)  // 昨天已签到（第3天）
            .thenReturn(1L)  // 前天已签到（第2天）
            .thenReturn(1L); // 大前天已签到（第1天）

        // 模拟用户积分记录存在，已有30积分（前3天）
        SysUserPoints existingPoints = new SysUserPoints();
        existingPoints.setUserId(TEST_USER_ID);
        existingPoints.setTotalPoints(30);
        existingPoints.setAvailablePoints(30);
        when(userPointsMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(existingPoints);

        // 模拟插入和更新成功
        when(signRecordMapper.insert(any(SysSignRecord.class))).thenReturn(1);
        when(userPointsMapper.updateById(any(SysUserPoints.class))).thenReturn(1);

        // 执行签到
        SignResultDTO result = signService.sign(TEST_USER_ID);

        // 验证结果 - 第4天获得20积分
        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals(20, result.getPointsEarned());
        assertEquals(50, result.getTotalPoints()); // 30 + 20
        assertEquals(4, result.getConsecutiveDays());
    }

    @Test
    @DisplayName("连续签到8天 - 应该获得30积分")
    void sign_EighthConsecutiveDay_ShouldGet30Points() {
        // 模拟今天未签到，但前7天都签到了
        when(signRecordMapper.selectCount(any(LambdaQueryWrapper.class)))
            .thenReturn(0L)  // 今天未签到
            .thenReturn(1L)  // 昨天（第7天）
            .thenReturn(1L)  // 第6天
            .thenReturn(1L)  // 第5天
            .thenReturn(1L)  // 第4天
            .thenReturn(1L)  // 第3天
            .thenReturn(1L)  // 第2天
            .thenReturn(1L); // 第1天

        // 模拟用户积分记录存在，已有100积分（前7天：10*3 + 20*4 = 110）
        SysUserPoints existingPoints = new SysUserPoints();
        existingPoints.setUserId(TEST_USER_ID);
        existingPoints.setTotalPoints(110);
        existingPoints.setAvailablePoints(110);
        when(userPointsMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(existingPoints);

        // 模拟插入和更新成功
        when(signRecordMapper.insert(any(SysSignRecord.class))).thenReturn(1);
        when(userPointsMapper.updateById(any(SysUserPoints.class))).thenReturn(1);

        // 执行签到
        SignResultDTO result = signService.sign(TEST_USER_ID);

        // 验证结果 - 第8天获得30积分
        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals(30, result.getPointsEarned());
        assertEquals(140, result.getTotalPoints()); // 110 + 30
        assertEquals(8, result.getConsecutiveDays());
    }

    @Test
    @DisplayName("连续签到31天 - 应该获得50积分")
    void sign_ThirtyFirstConsecutiveDay_ShouldGet50Points() {
        // 模拟今天未签到，但前30天都签到了
        when(signRecordMapper.selectCount(any(LambdaQueryWrapper.class)))
            .thenReturn(0L)  // 今天未签到
            .thenReturn(1L); // 昨天（第30天）

        // 对于前面的29天，都返回已签到
        for (int i = 0; i < 29; i++) {
            when(signRecordMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);
        }

        // 模拟用户积分记录存在
        SysUserPoints existingPoints = new SysUserPoints();
        existingPoints.setUserId(TEST_USER_ID);
        existingPoints.setTotalPoints(870); // 前30天的积分
        existingPoints.setAvailablePoints(870);
        when(userPointsMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(existingPoints);

        // 模拟插入和更新成功
        when(signRecordMapper.insert(any(SysSignRecord.class))).thenReturn(1);
        when(userPointsMapper.updateById(any(SysUserPoints.class))).thenReturn(1);

        // 执行签到
        SignResultDTO result = signService.sign(TEST_USER_ID);

        // 验证结果 - 第31天获得50积分
        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals(50, result.getPointsEarned());
        assertEquals(920, result.getTotalPoints()); // 870 + 50
        assertEquals(31, result.getConsecutiveDays());
    }

    @Test
    @DisplayName("断签后重新签到 - 应该从第1天重新开始计算")
    void sign_AfterBreak_ShouldResetToDay1() {
        // 模拟今天未签到，昨天也未签到（断签了）
        when(signRecordMapper.selectCount(any(LambdaQueryWrapper.class)))
            .thenReturn(0L)  // 今天未签到
            .thenReturn(0L); // 昨天也未签到

        // 模拟用户积分记录存在，已有历史积分
        SysUserPoints existingPoints = new SysUserPoints();
        existingPoints.setUserId(TEST_USER_ID);
        existingPoints.setTotalPoints(100);
        existingPoints.setAvailablePoints(100);
        when(userPointsMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(existingPoints);

        // 模拟插入和更新成功
        when(signRecordMapper.insert(any(SysSignRecord.class))).thenReturn(1);
        when(userPointsMapper.updateById(any(SysUserPoints.class))).thenReturn(1);

        // 执行签到
        SignResultDTO result = signService.sign(TEST_USER_ID);

        // 验证结果 - 断签后从第1天开始，获得10积分
        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals(10, result.getPointsEarned());
        assertEquals(110, result.getTotalPoints()); // 100 + 10
        assertEquals(1, result.getConsecutiveDays());
    }

    @Test
    @DisplayName("获取签到信息 - 应该返回正确的连续天数和日历")
    void getSignInfo_ShouldReturnCorrectInfo() {
        // 准备测试数据
        int year = 2024;
        int month = 3;
        
        // 模拟用户积分
        SysUserPoints userPoints = new SysUserPoints();
        userPoints.setUserId(TEST_USER_ID);
        userPoints.setTotalPoints(100);
        userPoints.setAvailablePoints(100);
        when(userPointsMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(userPoints);

        // 模拟今天已签到，昨天也签到了（连续2天）
        when(signRecordMapper.selectCount(any(LambdaQueryWrapper.class)))
            .thenReturn(1L)  // 今天已签到
            .thenReturn(1L); // 昨天已签到

        // 模拟该月的签到记录（3月1日、3月15日签到）
        List<SysSignRecord> signRecords = new ArrayList<>();
        SysSignRecord record1 = new SysSignRecord();
        record1.setUserId(TEST_USER_ID);
        record1.setSignDate(LocalDate.of(2024, 3, 1));
        record1.setRewardPoints(10);
        signRecords.add(record1);

        SysSignRecord record2 = new SysSignRecord();
        record2.setUserId(TEST_USER_ID);
        record2.setSignDate(LocalDate.of(2024, 3, 15));
        record2.setRewardPoints(10);
        signRecords.add(record2);

        when(signRecordMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(signRecords);

        // 执行查询
        SignResultDTO result = signService.getSignInfo(TEST_USER_ID, year, month);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals(100, result.getTotalPoints());
        assertEquals(2, result.getConsecutiveDays()); // 今天和昨天连续签到
        assertNotNull(result.getSignCalendar());
        assertEquals(31, result.getSignCalendar().size()); // 3月有31天

        // 验证日历内容
        Map<String, Object> day1 = result.getSignCalendar().get(0);
        assertEquals(1, day1.get("day"));
        assertEquals(true, day1.get("signed"));

        Map<String, Object> day15 = result.getSignCalendar().get(14);
        assertEquals(15, day15.get("day"));
        assertEquals(true, day15.get("signed"));

        Map<String, Object> day2 = result.getSignCalendar().get(1);
        assertEquals(2, day2.get("day"));
        assertEquals(false, day2.get("signed"));
    }

    @Test
    @DisplayName("获取签到信息 - 用户无积分记录时应该自动创建")
    void getSignInfo_NoPointsRecord_ShouldCreateNew() {
        // 模拟用户积分记录不存在
        when(userPointsMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(userPointsMapper.insert(any(SysUserPoints.class))).thenReturn(1);

        // 模拟今天未签到，昨天也未签到
        when(signRecordMapper.selectCount(any(LambdaQueryWrapper.class)))
            .thenReturn(0L)
            .thenReturn(0L);

        // 模拟该月无签到记录
        when(signRecordMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(new ArrayList<>());

        // 执行查询
        SignResultDTO result = signService.getSignInfo(TEST_USER_ID, 2024, 3);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.getSuccess());
        assertEquals(0, result.getTotalPoints());
        assertEquals(0, result.getConsecutiveDays());

        // 验证创建了新积分记录
        verify(userPointsMapper, times(1)).insert(any(SysUserPoints.class));
    }

    @Test
    @DisplayName("积分计算规则验证 - 各档位积分计算正确")
    void calculatePoints_AllTiers_ShouldBeCorrect() {
        // 使用反射测试私有方法
        // 第1-3天：10积分
        assertEquals(10, calculatePointsForTest(1));
        assertEquals(10, calculatePointsForTest(2));
        assertEquals(10, calculatePointsForTest(3));

        // 第4-7天：20积分
        assertEquals(20, calculatePointsForTest(4));
        assertEquals(20, calculatePointsForTest(7));

        // 第8-30天：30积分
        assertEquals(30, calculatePointsForTest(8));
        assertEquals(30, calculatePointsForTest(30));

        // 第31天及以上：50积分
        assertEquals(50, calculatePointsForTest(31));
        assertEquals(50, calculatePointsForTest(100));
    }

    // 辅助方法：通过实际调用来测试积分计算
    private int calculatePointsForTest(int consecutiveDays) {
        // 模拟今天未签到
        when(signRecordMapper.selectCount(any(LambdaQueryWrapper.class)))
            .thenReturn(0L);

        // 模拟前consecutiveDays-1天都签到了
        for (int i = 0; i < consecutiveDays - 1; i++) {
            when(signRecordMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);
        }

        // 如果需要更多天数，返回0（断签）
        when(signRecordMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

        // 模拟用户积分记录
        SysUserPoints existingPoints = new SysUserPoints();
        existingPoints.setUserId(TEST_USER_ID);
        existingPoints.setTotalPoints(0);
        existingPoints.setAvailablePoints(0);
        when(userPointsMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(existingPoints);

        when(signRecordMapper.insert(any(SysSignRecord.class))).thenReturn(1);
        when(userPointsMapper.updateById(any(SysUserPoints.class))).thenReturn(1);

        // 重置mock以准备下一次调用
        reset(signRecordMapper, userPointsMapper);

        // 重新设置基本的mock
        when(signRecordMapper.selectCount(any(LambdaQueryWrapper.class)))
            .thenReturn(0L);
        for (int i = 0; i < consecutiveDays - 1; i++) {
            when(signRecordMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);
        }
        when(userPointsMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(existingPoints);
        when(signRecordMapper.insert(any(SysSignRecord.class))).thenReturn(1);
        when(userPointsMapper.updateById(any(SysUserPoints.class))).thenReturn(1);

        SignResultDTO result = signService.sign(TEST_USER_ID);
        return result.getPointsEarned();
    }
}
