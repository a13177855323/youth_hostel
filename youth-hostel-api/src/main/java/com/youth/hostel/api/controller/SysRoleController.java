package com.youth.hostel.api.controller;

import com.youth.hostel.common.annotation.RequiresPermission;
import com.youth.hostel.common.context.UserContextHolder;
import com.youth.hostel.common.result.Result;
import com.youth.hostel.entity.po.SysRole;
import com.youth.hostel.service.SysRoleService;
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

    private final SysRoleService sysRoleService;

    @Operation(summary = "获取当前用户的角色列表")
    @GetMapping("/my")
    @RequiresPermission
    public Result<List<SysRole>> getMyRoles() {
        Long currentUserId = UserContextHolder.getUserId();
        return Result.success(sysRoleService.getUserRoles(currentUserId));
    }

    @Operation(summary = "获取指定用户的角色列表（仅管理员）")
    @GetMapping("/user/{userId}")
    @RequiresPermission(requireAdmin = true)
    public Result<List<SysRole>> getUserRoles(@PathVariable Long userId) {
        return Result.success(sysRoleService.getUserRoles(userId));
    }

    @Operation(summary = "分配角色（仅管理员）")
    @PostMapping("/assign")
    @RequiresPermission(requireAdmin = true)
    public Result<Void> assignRole(@RequestParam Long userId, @RequestParam Long roleId) {
        sysRoleService.assignRole(userId, roleId);
        return Result.success();
    }
}
