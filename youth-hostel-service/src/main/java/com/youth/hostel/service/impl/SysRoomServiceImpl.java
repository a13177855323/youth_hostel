package com.youth.hostel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youth.hostel.common.exception.BusinessException;
import com.youth.hostel.dao.mapper.SysRoomMapper;
import com.youth.hostel.entity.dto.RoomBookDTO;
import com.youth.hostel.entity.po.SysRoom;
import com.youth.hostel.service.SysRoomService;
import org.springframework.stereotype.Service;

@Service
public class SysRoomServiceImpl extends ServiceImpl<SysRoomMapper, SysRoom> implements SysRoomService {

    @Override
    public void bookRoom(RoomBookDTO bookDTO) {
        // 1. 检查房间是否存在
        SysRoom room = baseMapper.selectById(bookDTO.getRoomId());
        if (room == null) {
            throw new BusinessException("房间不存在");
        }

        // 2. 原子扣减库存（利用数据库UPDATE的原子性解决并发超卖问题）
        // UPDATE语句在数据库层面是原子操作，会自动加行锁，避免并发冲突
        int affectedRows = baseMapper.decreaseStock(bookDTO.getRoomId(), bookDTO.getQuantity());
        
        // 3. 如果影响行数为0，说明库存不足（并发时其他请求已经扣减了库存）
        if (affectedRows == 0) {
            throw new BusinessException("库存不足");
        }
    }
}
