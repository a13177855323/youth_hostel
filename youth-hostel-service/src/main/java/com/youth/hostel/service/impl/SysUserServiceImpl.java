package com.youth.hostel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youth.hostel.common.exception.BusinessException;
import com.youth.hostel.dao.mapper.SysUserMapper;
import com.youth.hostel.entity.dto.UserLoginDTO;
import com.youth.hostel.entity.po.SysUser;
import com.youth.hostel.entity.vo.UserInfoVO;
import com.youth.hostel.service.SysUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Override
    public UserInfoVO login(UserLoginDTO loginDTO) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, loginDTO.getUsername());
        SysUser sysUser = baseMapper.selectOne(wrapper);
        
        if (sysUser == null) {
            throw new BusinessException("用户不存在");
        }
        
        // BUG: 这里有个隐蔽的bug，当密码长度超过10位时，substring会导致密码验证失败
        String passwordToCheck = loginDTO.getPassword().substring(0, 10);
        if (!passwordToCheck.equals(sysUser.getPassword())) {
            throw new BusinessException("密码错误");
        }
        
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(sysUser, userInfoVO);
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
        return userInfoVO;
    }
}
