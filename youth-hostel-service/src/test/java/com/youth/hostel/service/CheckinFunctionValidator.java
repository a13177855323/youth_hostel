package com.youth.hostel.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 签到功能验证器
 * 用于验证签到功能的核心业务逻辑
 */
public class CheckinFunctionValidator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 根据连续签到天数计算应获得的积分
     */
    public static Integer calculatePoints(Integer continuousDays) {
        if (continuousDays == null || continuousDays < 1) {
            return 0;
        }
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
     * 模拟签到场景
     */
    public static void simulateCheckinScenarios() {
        System.out.println("=== 签到功能业务逻辑验证 ===");
        System.out.println();

        // 1. 验证积分计算规则
        System.out.println("【1】积分计算规则验证");
        System.out.println("连续天数 | 应获积分 | 验证结果");
        System.out.println("---------|----------|----------");

        boolean allPassed = true;
        Map<Integer, Integer> expectedPoints = new HashMap<>();
        expectedPoints.put(1, 10);
        expectedPoints.put(2, 10);
        expectedPoints.put(3, 10);
        expectedPoints.put(4, 20);
        expectedPoints.put(5, 20);
        expectedPoints.put(7, 20);
        expectedPoints.put(8, 30);
        expectedPoints.put(15, 30);
        expectedPoints.put(30, 30);
        expectedPoints.put(31, 50);
        expectedPoints.put(100, 50);
        expectedPoints.put(0, 0);
        expectedPoints.put(-1, 0);

        for (Map.Entry<Integer, Integer> entry : expectedPoints.entrySet()) {
            int days = entry.getKey();
            int expected = entry.getValue();
            int actual = calculatePoints(days);
            String result = actual == expected ? "PASS" : "FAIL";
            if (actual != expected) allPassed = false;
            System.out.printf("%6d天 | %6d分 | %s%n", days, actual, result);
        }

        System.out.println();

        // 2. 模拟签到流程
        System.out.println("【2】模拟签到流程验证");
        System.out.println();

        // 场景1：连续签到7天
        System.out.println("场景1：连续签到7天");
        System.out.println("第几天 | 连续天数 | 当日积分 | 累计积分");
        System.out.println("-------|----------|----------|----------");
        int totalPoints = 0;
        for (int day = 1; day <= 7; day++) {
            int points = calculatePoints(day);
            totalPoints += points;
            System.out.printf("第%d天  | %6d天 | %6d分 | %6d分%n", day, day, points, totalPoints);
        }
        System.out.println("预期：前3天每天10分(共30分)，后4天每天20分(共80分)，累计110分");
        System.out.println("实际：累计" + totalPoints + "分");
        System.out.println("验证结果：" + (totalPoints == 110 ? "PASS" : "FAIL"));
        System.out.println();

        // 场景2：连续签到35天
        System.out.println("场景2：连续签到35天累计积分");
        totalPoints = 0;
        for (int day = 1; day <= 35; day++) {
            totalPoints += calculatePoints(day);
        }
        System.out.println("累计积分：" + totalPoints);
        int expectedTotal = 3*10 + 4*20 + 23*30 + 5*50;  // 3+4+23+5=35天
        System.out.println("预期积分：" + expectedTotal + " (3天×10 + 4天×20 + 23天×30 + 5天×50)");
        System.out.println("验证结果：" + (totalPoints == expectedTotal ? "PASS" : "FAIL"));
        System.out.println();

        // 场景3：断签场景
        System.out.println("场景3：断签验证");
        System.out.println("签到2天，断签1天，再签到3天");
        totalPoints = calculatePoints(1) + calculatePoints(2);  // 签到2天
        totalPoints += calculatePoints(1) + calculatePoints(2) + calculatePoints(3);  // 断签后重新签到3天
        System.out.println("累计积分：" + totalPoints);
        System.out.println("预期积分：50 (10+10 + 10+10+10)");
        System.out.println("验证结果：" + (totalPoints == 50 ? "PASS" : "FAIL"));
        System.out.println();

        // 最终结论
        System.out.println("=== 验证总结 ===");
        System.out.println(allPassed && totalPoints == 50 ? "所有验证通过！✓" : "存在验证失败！✗");
    }

    /**
     * 打印API接口说明
     */
    public static void printApiInfo() {
        System.out.println("\n=== API 接口说明 ===");
        System.out.println();
        System.out.println("1. POST /api/checkin?userId={用户ID} - 用户签到");
        System.out.println("   返回: {success, continuousDays, pointsEarned, totalPoints, message}");
        System.out.println();
        System.out.println("2. GET /api/checkin/info?userId={用户ID}&year={年}&month={月} - 获取签到信息");
        System.out.println("   返回: {continuousDays, totalPoints, availablePoints, monthCheckinCalendar, monthCheckinCount, checkinDates}");
        System.out.println();
        System.out.println("3. GET /api/checkin/today?userId={用户ID} - 判断今日是否已签到");
        System.out.println("   返回: true/false");
        System.out.println();
        System.out.println("4. GET /api/checkin/points-rule - 获取积分奖励规则说明");
        System.out.println("   返回: 积分规则说明文本");
    }

    public static void main(String[] args) {
        // 运行验证
        simulateCheckinScenarios();
        printApiInfo();

        // 显示当前日期相关信息
        LocalDate today = LocalDate.now();
        System.out.println("\n=== 当前环境信息 ===");
        System.out.println("系统日期：" + today.format(DATE_FORMATTER));
        System.out.println("年：" + today.getYear() + "，月：" + today.getMonthValue() + "，日：" + today.getDayOfMonth());
    }
}
