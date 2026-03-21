package com.youth.hostel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youth.hostel.dao.mapper.SysUserMapper;
import com.youth.hostel.entity.dto.UserLoginDTO;
import com.youth.hostel.entity.po.SysUser;
import com.youth.hostel.entity.vo.UserInfoVO;
import com.youth.hostel.service.SysUserService;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Override
    public UserInfoVO login(UserLoginDTO loginDTO) {
        return null;
    }

    @Override
    public UserInfoVO getUserInfo(Long userId) {
        return null;
    }
}
