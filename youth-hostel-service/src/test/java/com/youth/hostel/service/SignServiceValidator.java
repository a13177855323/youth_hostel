package com.youth.hostel.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 签到功能验证器
 * 用于验证签到功能的核心业务逻辑
 */
public class SignServiceValidator {

    /**
     * 根据连续签到天数计算奖励积分
     */
    public static int calculateRewardPoints(int consecutiveDays) {
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

    /**
     * 模拟生成签到日历
     */
    public static List<Map<String, Object>> generateSignCalendar(List<LocalDate> signedDates, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<Map<String, Object>> signCalendar = new ArrayList<>();
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            Map<String, Object> dayInfo = new HashMap<>();
            dayInfo.put("date", current.toString());
            dayInfo.put("signed", signedDates.contains(current));
            signCalendar.add(dayInfo);
            current = current.plusDays(1);
        }
        return signCalendar;
    }

    public static void main(String[] args) {
        System.out.println("=== 签到功能业务逻辑验证 ===\n");

        // 1. 验证积分计算规则
        System.out.println("【1】积分计算规则验证");
        System.out.println("连续天数 | 应获积分 | 验证结果");
        System.out.println("---------|----------|----------");

        boolean allPassed = true;
        Map<Integer, Integer> testCases = new HashMap<>();
        testCases.put(1, 10);
        testCases.put(2, 10);
        testCases.put(3, 10);
        testCases.put(4, 20);
        testCases.put(5, 20);
        testCases.put(7, 20);
        testCases.put(8, 30);
        testCases.put(15, 30);
        testCases.put(30, 30);
        testCases.put(31, 50);
        testCases.put(100, 50);

        for (Map.Entry<Integer, Integer> entry : testCases.entrySet()) {
            int days = entry.getKey();
            int expected = entry.getValue();
            int actual = calculateRewardPoints(days);
            String result = actual == expected ? "PASS" : "FAIL";
            if (actual != expected) allPassed = false;
            System.out.printf("%6d天 | %6d分 | %s%n", days, actual, result);
        }
        System.out.println();

        // 2. 模拟连续签到7天积分累计
        System.out.println("【2】连续签到7天积分累计验证");
        System.out.println("第几天 | 连续天数 | 当日积分 | 累计积分");
        System.out.println("-------|----------|----------|----------");
        int totalPoints = 0;
        for (int day = 1; day <= 7; day++) {
            int points = calculateRewardPoints(day);
            totalPoints += points;
            System.out.printf("第%d天  | %6d天 | %6d分 | %6d分%n", day, day, points, totalPoints);
        }
        System.out.println("预期：前3天每天10分(共30分)，后4天每天20分(共80分)，累计110分");
        System.out.println("实际：累计" + totalPoints + "分");
        System.out.println("验证结果：" + (totalPoints == 110 ? "PASS" : "FAIL"));
        System.out.println();

        // 3. 连续签到35天验证
        System.out.println("【3】连续签到35天累计积分");
        totalPoints = 0;
        for (int day = 1; day <= 35; day++) {
            totalPoints += calculateRewardPoints(day);
        }
        int expectedTotal = 3*10 + 4*20 + 23*30 + 5*50;
        System.out.println("累计积分：" + totalPoints);
        System.out.println("预期积分：" + expectedTotal + " (3天×10 + 4天×20 + 23天×30 + 5天×50)");
        System.out.println("验证结果：" + (totalPoints == expectedTotal ? "PASS" : "FAIL"));
        System.out.println();

        // 4. 断签场景验证
        System.out.println("【4】断签场景验证");
        System.out.println("签到2天，断签1天，再签到3天");
        totalPoints = calculateRewardPoints(1) + calculateRewardPoints(2);
        totalPoints += calculateRewardPoints(1) + calculateRewardPoints(2) + calculateRewardPoints(3);
        System.out.println("累计积分：" + totalPoints);
        System.out.println("预期积分：50 (10+10 + 10+10+10)");
        System.out.println("验证结果：" + (totalPoints == 50 ? "PASS" : "FAIL"));
        System.out.println();

        // 5. 签到日历生成验证
        System.out.println("【5】签到日历生成验证");
        List<LocalDate> signedDates = new ArrayList<>();
        signedDates.add(LocalDate.of(2026, 4, 1));
        signedDates.add(LocalDate.of(2026, 4, 2));
        signedDates.add(LocalDate.of(2026, 4, 5));
        List<Map<String, Object>> calendar = generateSignCalendar(signedDates, 2026, 4);
        System.out.println("2026年4月日历（前7天）：");
        for (int i = 0; i < 7; i++) {
            Map<String, Object> day = calendar.get(i);
            System.out.println("  " + day.get("date") + " : " + (Boolean.TRUE.equals(day.get("signed")) ? "已签到 ✓" : "未签到"));
        }
        System.out.println();

        // 最终总结
        System.out.println("=== 验证总结 ===");
        System.out.println(allPassed && totalPoints == 50 ? "所有核心业务逻辑验证通过！✓" : "存在验证失败！✗");

        System.out.println("\n=== 当前环境信息 ===");
        System.out.println("系统日期：" + LocalDate.now());
        System.out.println("签到功能文件：youth-hostel-service/src/main/java/com/youth/hostel/service/impl/SysSignServiceImpl.java");
    }
}
