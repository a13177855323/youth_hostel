package com.youth.hostel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youth.hostel.common.exception.BusinessException;
import com.youth.hostel.dao.mapper.SysRoomMapper;
import com.youth.hostel.entity.dto.RoomBookDTO;
import com.youth.hostel.entity.po.SysRoom;
import com.youth.hostel.service.SysRoomService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SysRoomServiceImpl extends ServiceImpl<SysRoomMapper, SysRoom> implements SysRoomService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bookRoom(RoomBookDTO bookDTO) {
        SysRoom room = baseMapper.selectById(bookDTO.getRoomId());
        if (room == null) {
            throw new BusinessException("房间不存在");
        }

        if (room.getStock() < bookDTO.getQuantity()) {
            throw new BusinessException("库存不足");
        }

        room.setStock(room.getStock() - bookDTO.getQuantity());
        int affected = baseMapper.updateById(room);

        if (affected == 0) {
            throw new BusinessException("预订失败，请重试");
        }
    }
}
