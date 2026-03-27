package com.youth.hostel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youth.hostel.entity.po.SysSignIn;
import com.youth.hostel.entity.vo.SignInInfoVO;
import com.youth.hostel.entity.vo.SignInResultVO;

import java.time.YearMonth;

public interface SysSignInService extends IService<SysSignIn> {

    SignInResultVO signIn(Long userId);

    SignInInfoVO getSignInInfo(Long userId, YearMonth yearMonth);
}
