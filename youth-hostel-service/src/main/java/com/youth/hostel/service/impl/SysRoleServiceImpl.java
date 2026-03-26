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
        List<SysRole> roles = getUserRoles(userId);
        List<String> roleCodes = roles.stream().map(SysRole::getRoleCode).toList();

        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysPermission::getPermCode, permCode);
        List<SysPermission> permissions = permissionMapper.selectList(wrapper);

        boolean hasPermission = permissions.stream()
                .anyMatch(p -> roleCodes.contains("ROLE_ADMIN") || p.getPermCode().equals(permCode));

        if (!hasPermission) {
            throw new BusinessException("无权限");
        }
    }
}
