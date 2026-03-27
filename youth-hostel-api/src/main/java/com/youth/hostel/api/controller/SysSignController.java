package com.youth.hostel.api.controller;

import com.youth.hostel.common.exception.BusinessException;
import com.youth.hostel.common.result.Result;
import com.youth.hostel.entity.dto.SignResultDTO;
import com.youth.hostel.service.SysSignService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "签到管理")
@RestController
@RequestMapping("/api/sign")
@RequiredArgsConstructor
public class SysSignController {

    private final SysSignService sysSignService;

    @Operation(summary = "用户签到")
    @PostMapping("/do")
    public Result<SignResultDTO> sign(@Parameter(description = "用户ID") @RequestParam Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        return Result.success(sysSignService.sign(userId));
    }

    @Operation(summary = "获取签到信息")
    @GetMapping("/info")
    public Result<SignResultDTO> getSignInfo(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "年份，默认为当前年份") @RequestParam(required = false) Integer year,
            @Parameter(description = "月份，默认为当前月份") @RequestParam(required = false) Integer month) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        // 默认为当前年月
        if (year == null) {
            year = LocalDate.now().getYear();
        }
        if (month == null) {
            month = LocalDate.now().getMonthValue();
        }

        // 验证月份范围
        if (month < 1 || month > 12) {
            throw new BusinessException("月份必须在1-12之间");
        }

        return Result.success(sysSignService.getSignInfo(userId, year, month));
    }
}
