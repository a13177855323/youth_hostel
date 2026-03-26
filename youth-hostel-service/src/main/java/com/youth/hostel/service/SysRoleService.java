package com.youth.hostel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youth.hostel.entity.po.SysRole;

import java.util.List;

public interface SysRoleService extends IService<SysRole> {

    List<SysRole> getUserRoles(Long currentUserId, Long targetUserId);

    void assignRole(Long currentUserId, Long userId, Long roleId);

    void checkPermission(Long userId, String permCode);
}
