package com.youth.hostel.api.controller;

import com.youth.hostel.common.result.Result;
import com.youth.hostel.entity.vo.CheckinInfoVO;
import com.youth.hostel.entity.vo.CheckinResultVO;
import com.youth.hostel.service.SysCheckinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 签到管理控制器
 */
@Tag(name = "签到管理")
@RestController
@RequestMapping("/api/checkin")
@RequiredArgsConstructor
public class SysCheckinController {

    private final SysCheckinService checkinService;

    /**
     * 用户签到
     * 注意：实际项目中应该从登录上下文获取当前用户ID，
     * 这里简化处理，通过参数传入或从请求头获取
     */
    @Operation(summary = "用户签到")
    @PostMapping
    public Result<CheckinResultVO> checkin(
            @Parameter(description = "用户ID") @RequestParam(required = false) Long userId) {
        // 实际项目中应该从上下文获取当前登录用户ID
        // 这里简化处理，实际应该使用：Long currentUserId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.failed("用户ID不能为空");
        }
        CheckinResultVO result = checkinService.checkin(userId);
        if (result.getSuccess()) {
            return Result.success(result);
        } else {
            return Result.failed(result.getMessage());
        }
    }

    @Operation(summary = "获取签到信息")
    @GetMapping("/info")
    public Result<CheckinInfoVO> getCheckinInfo(
            @Parameter(description = "用户ID") @RequestParam(required = false) Long userId,
            @Parameter(description = "年份（默认当前年）") @RequestParam(required = false) Integer year,
            @Parameter(description = "月份（默认当前月）") @RequestParam(required = false) Integer month) {
        if (userId == null) {
            return Result.failed("用户ID不能为空");
        }
        CheckinInfoVO info = checkinService.getCheckinInfo(userId, year, month);
        return Result.success(info);
    }

    @Operation(summary = "判断今日是否已签到")
    @GetMapping("/today")
    public Result<Boolean> isTodayCheckedIn(
            @Parameter(description = "用户ID") @RequestParam(required = false) Long userId) {
        if (userId == null) {
            return Result.failed("用户ID不能为空");
        }
        Boolean checkedIn = checkinService.isTodayCheckedIn(userId);
        return Result.success(checkedIn);
    }

    @Operation(summary = "获取积分奖励规则说明")
    @GetMapping("/points-rule")
    public Result<String> getPointsRule() {
        String rule = """
                连续签到积分奖励规则：
                - 第1-3天：每天 10 积分
                - 第4-7天：每天 20 积分
                - 第8-30天：每天 30 积分
                - 31天及以上：每天 50 积分
                断签后重新从第1天计算
                """;
        return Result.success(rule);
    }
}
