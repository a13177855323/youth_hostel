package com.youth.hostel.api.controller;

import com.youth.hostel.common.annotation.RequiresPermission;
import com.youth.hostel.common.context.UserContextHolder;
import com.youth.hostel.common.result.Result;
import com.youth.hostel.entity.dto.UserLoginDTO;
import com.youth.hostel.entity.vo.UserInfoVO;
import com.youth.hostel.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户管理")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService sysUserService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    @RequiresPermission(requireLogin = false)
    public Result<UserInfoVO> login(@RequestBody UserLoginDTO loginDTO) {
        return Result.success(sysUserService.login(loginDTO));
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    @RequiresPermission
    public Result<UserInfoVO> getCurrentUserInfo() {
        Long currentUserId = UserContextHolder.getUserId();
        return Result.success(sysUserService.getUserInfo(currentUserId));
    }

    @Operation(summary = "获取指定用户信息（仅管理员）")
    @GetMapping("/info/{userId}")
    @RequiresPermission(requireAdmin = true)
    public Result<UserInfoVO> getUserInfo(@PathVariable Long userId) {
        return Result.success(sysUserService.getUserInfo(userId));
    }
}
