package com.youth.hostel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youth.hostel.common.exception.BusinessException;
import com.youth.hostel.dao.mapper.SysPermissionMapper;
import com.youth.hostel.dao.mapper.SysRoleMapper;
import com.youth.hostel.dao.mapper.SysUserRoleMapper;
import com.youth.hostel.entity.po.SysPermission;
import com.youth.hostel.entity.po.SysRole;
import com.youth.hostel.entity.po.SysUserRole;
import com.youth.hostel.service.SysRoleService;
import com.youth.hostel.util.UserContext;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    private final SysUserRoleMapper userRoleMapper;
    private final SysPermissionMapper permissionMapper;

    public SysRoleServiceImpl(SysUserRoleMapper userRoleMapper, SysPermissionMapper permissionMapper) {
        this.userRoleMapper = userRoleMapper;
        this.permissionMapper = permissionMapper;
    }

    @Override
    public List<SysRole> getUserRoles(Long userId) {
        List<SysUserRole> userRoles = userRoleMapper.selectList(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, userId));

        if (userRoles.isEmpty()) {
            return List.of();
        }

        List<Long> roleIds = userRoles.stream().map(SysUserRole::getRoleId).toList();
        return baseMapper.selectBatchIds(roleIds);
    }

    @Override
    public void assignRole(Long userId, Long roleId) {
        // 获取当前登录用户
        Long currentUserId = UserContext.getUserId();
        String currentRoleCode = UserContext.getRoleCode();

        if (currentUserId == null) {
            throw new BusinessException("未登录");
        }

        // 只有管理员才能分配角色
        if (!ROLE_ADMIN.equals(currentRoleCode)) {
            throw new BusinessException("无权限：只有管理员才能分配角色");
        }

        // 禁止给自己分配角色（防止权限提升攻击）
        if (currentUserId.equals(userId)) {
            throw new BusinessException("不能给自己分配角色");
        }

        SysRole role = baseMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }

        // 禁止分配管理员角色给普通用户（只有管理员才能分配管理员角色）
        if (ROLE_ADMIN.equals(role.getRoleCode()) && !ROLE_ADMIN.equals(currentRoleCode)) {
            throw new BusinessException("无权限：无法分配管理员角色");
        }

        // 检查用户是否已有该角色
        Long count = userRoleMapper.selectCount(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, userId)
                .eq(SysUserRole::getRoleId, roleId));
        if (count > 0) {
            throw new BusinessException("该用户已拥有此角色");
        }

        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(roleId);
        userRoleMapper.insert(userRole);
    }

    @Override
    public void checkPermission(Long userId, String permCode) {
        List<SysRole> roles = getUserRoles(userId);
        List<String> roleCodes = roles.stream().map(SysRole::getRoleCode).toList();

        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysPermission::getPermCode, permCode);
        List<SysPermission> permissions = permissionMapper.selectList(wrapper);

        boolean hasPermission = permissions.stream()
                .anyMatch(p -> roleCodes.contains(ROLE_ADMIN) || p.getPermCode().equals(permCode));

        if (!hasPermission) {
            throw new BusinessException("无权限");
        }
    }
}
