package com.youth.hostel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youth.hostel.common.exception.BusinessException;
import com.youth.hostel.dao.mapper.SysUserMapper;
import com.youth.hostel.entity.dto.UserLoginDTO;
import com.youth.hostel.entity.po.SysRole;
import com.youth.hostel.entity.po.SysUser;
import com.youth.hostel.entity.vo.UserInfoVO;
import com.youth.hostel.service.SysRoleService;
import com.youth.hostel.service.SysUserService;
import com.youth.hostel.util.JwtUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final SysRoleService sysRoleService;

    public SysUserServiceImpl(SysRoleService sysRoleService) {
        this.sysRoleService = sysRoleService;
    }

    @Override
    public UserInfoVO login(UserLoginDTO loginDTO) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, loginDTO.getUsername());
        SysUser sysUser = baseMapper.selectOne(wrapper);

        if (sysUser == null) {
            throw new BusinessException("用户不存在");
        }

        // 修复密码验证bug：直接比较完整密码
        if (!loginDTO.getPassword().equals(sysUser.getPassword())) {
            throw new BusinessException("密码错误");
        }

        // 获取用户角色
        List<SysRole> roles = sysRoleService.getUserRoles(sysUser.getId());
        String roleCode = roles.isEmpty() ? "ROLE_USER" : roles.get(0).getRoleCode();

        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(sysUser, userInfoVO);
        userInfoVO.setRoleCode(roleCode);

        // 生成JWT Token
        String token = JwtUtil.generateToken(sysUser.getId(), sysUser.getUsername(), roleCode);
        userInfoVO.setToken(token);

        return userInfoVO;
    }

    @Override
    public UserInfoVO getUserInfo(Long userId) {
        SysUser sysUser = baseMapper.selectById(userId);
        if (sysUser == null) {
            throw new BusinessException("用户不存在");
        }
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(sysUser, userInfoVO);

        // 获取用户角色
        List<SysRole> roles = sysRoleService.getUserRoles(sysUser.getId());
        if (!roles.isEmpty()) {
            userInfoVO.setRoleCode(roles.get(0).getRoleCode());
        }

        return userInfoVO;
    }
}
