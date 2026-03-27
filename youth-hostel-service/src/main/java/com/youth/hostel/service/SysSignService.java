package com.youth.hostel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youth.hostel.entity.dto.SignResultDTO;
import com.youth.hostel.entity.po.SysSignRecord;

import java.time.LocalDate;

public interface SysSignService extends IService<SysSignRecord> {

    SignResultDTO sign(Long userId);

    SignResultDTO getSignInfo(Long userId, Integer year, Integer month);
}
