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
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    private final SysUserRoleMapper userRoleMapper;
    private final SysPermissionMapper permissionMapper;

    public SysRoleServiceImpl(SysUserRoleMapper userRoleMapper, SysPermissionMapper permissionMapper) {
        this.userRoleMapper = userRoleMapper;
        this.permissionMapper = permissionMapper;
    }

    @Override
    public List<SysRole> getUserRoles(Long currentUserId, Long targetUserId) {
        if (!currentUserId.equals(targetUserId) && !isAdmin(currentUserId)) {
            throw new BusinessException("无权限查看其他用户角色");
        }

        List<SysUserRole> userRoles = userRoleMapper.selectList(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, targetUserId));

        if (userRoles.isEmpty()) {
            return List.of();
        }

        List<Long> roleIds = userRoles.stream().map(SysUserRole::getRoleId).toList();
        return baseMapper.selectBatchIds(roleIds);
    }

    @Override
    public void assignRole(Long currentUserId, Long userId, Long roleId) {
        if (!isAdmin(currentUserId)) {
            throw new BusinessException("只有管理员才能分配角色");
        }

        SysRole role = baseMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }

        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(roleId);
        userRoleMapper.insert(userRole);
    }

    @Override
    public void checkPermission(Long userId, String permCode) {
        if (isAdmin(userId)) {
            return;
        }

        List<SysRole> roles = getUserRoles(userId, userId);
        if (roles.isEmpty()) {
            throw new BusinessException("无权限");
        }

        List<Long> roleIds = roles.stream().map(SysRole::getId).toList();

        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysPermission::getRoleId, roleIds)
                .eq(SysPermission::getPermCode, permCode);

        Long count = permissionMapper.selectCount(wrapper);
        if (count == null || count == 0) {
            throw new BusinessException("无权限");
        }
    }

    private boolean isAdmin(Long userId) {
        List<SysUserRole> userRoles = userRoleMapper.selectList(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, userId));

        if (userRoles.isEmpty()) {
            return false;
        }

        List<Long> roleIds = userRoles.stream().map(SysUserRole::getRoleId).toList();
        List<SysRole> roles = baseMapper.selectBatchIds(roleIds);

        return roles.stream().anyMatch(role -> "ROLE_ADMIN".equals(role.getRoleCode()));
    }
}
