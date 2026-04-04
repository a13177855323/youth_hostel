package com.youth.hostel.api.controller;

import com.youth.hostel.common.exception.BusinessException;
import com.youth.hostel.common.result.Result;
import com.youth.hostel.entity.dto.UserLoginDTO;
import com.youth.hostel.entity.vo.UserInfoVO;
import com.youth.hostel.service.SysUserService;
import com.youth.hostel.util.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户管理")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class SysUserController {

    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    private final SysUserService sysUserService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<UserInfoVO> login(@RequestBody UserLoginDTO loginDTO) {
        return Result.success(sysUserService.login(loginDTO));
    }

    @Operation(summary = "获取当前登录用户信息")
    @GetMapping("/info")
    public Result<UserInfoVO> getMyInfo() {
        Long currentUserId = UserContext.getUserId();
        if (currentUserId == null) {
            throw new BusinessException("未登录");
        }
        return Result.success(sysUserService.getUserInfo(currentUserId));
    }

    @Operation(summary = "获取指定用户信息（仅管理员或本人）")
    @GetMapping("/info/{userId}")
    public Result<UserInfoVO> getUserInfo(@PathVariable Long userId) {
        Long currentUserId = UserContext.getUserId();
        String currentRoleCode = UserContext.getRoleCode();

        if (currentUserId == null) {
            throw new BusinessException("未登录");
        }

        // 权限检查：只有管理员或本人可以查看用户信息
        if (!ROLE_ADMIN.equals(currentRoleCode) && !currentUserId.equals(userId)) {
            throw new BusinessException("无权限：只能查看自己的信息");
        }

        return Result.success(sysUserService.getUserInfo(userId));
    }
}
