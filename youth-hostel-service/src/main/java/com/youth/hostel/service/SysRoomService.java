package com.youth.hostel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youth.hostel.entity.po.SysRoom;
import com.youth.hostel.entity.dto.RoomBookDTO;

public interface SysRoomService extends IService<SysRoom> {

    void bookRoom(RoomBookDTO bookDTO);
}
