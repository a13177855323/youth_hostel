package com.youth.hostel.api.controller;

import com.youth.hostel.common.result.Result;
import com.youth.hostel.entity.vo.SignInInfoVO;
import com.youth.hostel.entity.vo.SignInResultVO;
import com.youth.hostel.service.SysSignInService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;

@Tag(name = "签到管理")
@RestController
@RequestMapping("/api/signin")
@RequiredArgsConstructor
public class SysSignInController {

    private final SysSignInService signInService;

    @Operation(summary = "用户签到")
    @PostMapping("/{userId}")
    public Result<SignInResultVO> signIn(@PathVariable Long userId) {
        return Result.success(signInService.signIn(userId));
    }

    @Operation(summary = "获取签到信息")
    @GetMapping("/{userId}/info")
    public Result<SignInInfoVO> getSignInInfo(
            @PathVariable Long userId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth) {
        return Result.success(signInService.getSignInInfo(userId, yearMonth));
    }
}
