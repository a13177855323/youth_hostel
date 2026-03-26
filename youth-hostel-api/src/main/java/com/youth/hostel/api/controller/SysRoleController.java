package com.youth.hostel.api.controller;

import com.youth.hostel.common.exception.BusinessException;
import com.youth.hostel.common.result.Result;
import com.youth.hostel.entity.po.SysRole;
import com.youth.hostel.service.SysRoleService;
import com.youth.hostel.util.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "角色管理")
@RestController
@RequestMapping("/api/role")
@RequiredArgsConstructor
public class SysRoleController {

    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    private final SysRoleService sysRoleService;

    @Operation(summary = "获取当前用户的角色信息")
    @GetMapping("/my-roles")
    public Result<List<SysRole>> getMyRoles() {
        Long currentUserId = UserContext.getUserId();
        if (currentUserId == null) {
            throw new BusinessException("未登录");
        }
        return Result.success(sysRoleService.getUserRoles(currentUserId));
    }

    @Operation(summary = "获取指定用户的角色信息（仅管理员）")
    @GetMapping("/user-roles/{userId}")
    public Result<List<SysRole>> getUserRoles(@PathVariable Long userId) {
        // 权限检查：只有管理员可以查看其他用户的角色
        String currentRoleCode = UserContext.getRoleCode();
        if (!ROLE_ADMIN.equals(currentRoleCode)) {
            throw new BusinessException("无权限：只有管理员可以查看其他用户的角色信息");
        }
        return Result.success(sysRoleService.getUserRoles(userId));
    }

    @Operation(summary = "分配角色（仅管理员）")
    @PostMapping("/assign")
    public Result<Void> assignRole(@RequestParam Long userId, @RequestParam Long roleId) {
        sysRoleService.assignRole(userId, roleId);
        return Result.success();
    }
}
