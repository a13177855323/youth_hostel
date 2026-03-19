package com.youth.hostel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youth.hostel.entity.po.SysUser;
import com.youth.hostel.entity.dto.UserLoginDTO;
import com.youth.hostel.entity.vo.UserInfoVO;

public interface SysUserService extends IService<SysUser> {

    UserInfoVO login(UserLoginDTO loginDTO);

    UserInfoVO getUserInfo(Long userId);
}
