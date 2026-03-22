package com.youth.hostel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youth.hostel.entity.po.SysOrder;

public interface SysOrderService extends IService<SysOrder> {

    String createOrder(Long userId, java.math.BigDecimal totalAmount);

    void payOrder(String orderNo);
}
