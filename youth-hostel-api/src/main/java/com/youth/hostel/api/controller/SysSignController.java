package com.youth.hostel.api.controller;

import com.youth.hostel.common.result.Result;
import com.youth.hostel.entity.dto.SignResultDTO;
import com.youth.hostel.service.SysSignService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "签到管理")
@RestController
@RequestMapping("/api/sign")
@RequiredArgsConstructor
public class SysSignController {

    private final SysSignService signService;

    @Operation(summary = "签到")
    @PostMapping("/signIn")
    public Result<SignResultDTO> sign(@RequestParam Long userId) {
        return Result.success(signService.sign(userId));
    }

    @Operation(summary = "获取签到信息")
    @GetMapping("/info")
    public Result<SignResultDTO> getSignInfo(
            @RequestParam Long userId,
            @RequestParam Integer year,
            @RequestParam Integer month) {
        return Result.success(signService.getSignInfo(userId, year, month));
    }
}
