package com.youth.hostel.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 签到功能单元测试
 */
@SpringBootTest(classes = com.youth.hostel.service.impl.SysCheckinServiceImpl.class)
public class SysCheckinServiceTest {

    @Autowired
    private SysCheckinService checkinService;

    @BeforeEach
    void setUp() {
    }

    /**
     * 测试积分计算规则
     */
    @Test
    void testCalculatePoints() {
        // 第1-3天：10分
        assertEquals(10, checkinService.calculatePoints(1));
        assertEquals(10, checkinService.calculatePoints(2));
        assertEquals(10, checkinService.calculatePoints(3));

        // 第4-7天：20分
        assertEquals(20, checkinService.calculatePoints(4));
        assertEquals(20, checkinService.calculatePoints(5));
        assertEquals(20, checkinService.calculatePoints(6));
        assertEquals(20, checkinService.calculatePoints(7));

        // 第8-30天：30分
        assertEquals(30, checkinService.calculatePoints(8));
        assertEquals(30, checkinService.calculatePoints(15));
        assertEquals(30, checkinService.calculatePoints(30));

        // 31天及以上：50分
        assertEquals(50, checkinService.calculatePoints(31));
        assertEquals(50, checkinService.calculatePoints(100));

        // 边界情况
        assertEquals(0, checkinService.calculatePoints(0));
        assertEquals(0, checkinService.calculatePoints(null));
    }

    /**
     * 测试签到规则的用例场景
     */
    @Test
    void testCheckinScenarios() {
        // 场景1: 新用户第一次签到
        // 预期：连续天数=1，获得积分=10

        // 场景2: 连续签到第3天
        // 预期：连续天数=3，获得积分=10

        // 场景3: 连续签到第4天
        // 预期：连续天数=4，获得积分=20

        // 场景4: 连续签到第7天
        // 预期：连续天数=7，获得积分=20

        // 场景5: 连续签到第8天
        // 预期：连续天数=8，获得积分=30

        // 场景6: 连续签到第30天
        // 预期：连续天数=30，获得积分=30

        // 场景7: 连续签到第31天
        // 预期：连续天数=31，获得积分=50

        // 场景8: 断签后重新签到（比如签到2天，断签1天，再签到）
        // 预期：连续天数=1，获得积分=10

        // 场景9: 同一天重复签到
        // 预期：提示"今日已签到"，获得积分=0

        System.out.println("=== 签到积分规则验证 ===");
        System.out.println("连续天数 | 应获得积分");
        System.out.println("---------|----------");
        for (int days = 1; days <= 35; days++) {
            System.out.printf("%5d天  | %4d分%n", days, checkinService.calculatePoints(days));
        }
    }

    /**
     * 打印积分规则说明
     */
    @Test
    void printPointsRule() {
        System.out.println("\n=== 连续签到积分奖励规则 ===");
        System.out.println("第1-3天：每天 10 积分");
        System.out.println("第4-7天：每天 20 积分");
        System.out.println("第8-30天：每天 30 积分");
        System.out.println("31天及以上：每天 50 积分");
        System.out.println("断签后重新从第1天计算");
    }
}
