package com.youth.hostel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youth.hostel.common.exception.BusinessException;
import com.youth.hostel.dao.mapper.SysSignRecordMapper;
import com.youth.hostel.dao.mapper.SysUserPointsMapper;
import com.youth.hostel.entity.dto.SignResultDTO;
import com.youth.hostel.entity.po.SysSignRecord;
import com.youth.hostel.entity.po.SysUserPoints;
import com.youth.hostel.service.SysSignService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SysSignServiceImpl extends ServiceImpl<SysSignRecordMapper, SysSignRecord> implements SysSignService {

    private final SysUserPointsMapper userPointsMapper;

    public SysSignServiceImpl(SysUserPointsMapper userPointsMapper) {
        this.userPointsMapper = userPointsMapper;
    }

    @Override
    public SignResultDTO sign(Long userId) {
        return null;
    }

    @Override
    public SignResultDTO getSignInfo(Long userId, Integer year, Integer month) {
        return null;
    }
}
