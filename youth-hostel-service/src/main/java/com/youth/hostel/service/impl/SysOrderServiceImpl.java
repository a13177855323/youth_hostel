package com.youth.hostel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youth.hostel.common.exception.BusinessException;
import com.youth.hostel.dao.mapper.SysOrderMapper;
import com.youth.hostel.dao.mapper.SysWalletMapper;
import com.youth.hostel.entity.po.SysOrder;
import com.youth.hostel.entity.po.SysWallet;
import com.youth.hostel.service.SysOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class SysOrderServiceImpl extends ServiceImpl<SysOrderMapper, SysOrder> implements SysOrderService {

    private final SysWalletMapper walletMapper;

    public SysOrderServiceImpl(SysWalletMapper walletMapper) {
        this.walletMapper = walletMapper;
    }

    @Override
    public String createOrder(Long userId, BigDecimal totalAmount) {
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("订单金额必须大于0");
        }

        String orderNo = UUID.randomUUID().toString().replace("-", "");
        SysOrder order = new SysOrder();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setStatus(0);
        baseMapper.insert(order);
        return orderNo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payOrder(String orderNo) {
        SysOrder order = baseMapper.selectOne(new LambdaQueryWrapper<SysOrder>()
                .eq(SysOrder::getOrderNo, orderNo));

        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        if (order.getStatus() == 1) {
            throw new BusinessException("订单已支付");
        }

        int affectedRows = walletMapper.deductBalance(order.getUserId(), order.getTotalAmount());
        if (affectedRows == 0) {
            throw new BusinessException("余额不足");
        }

        order.setStatus(1);
        order.setPayTime(LocalDateTime.now());
        baseMapper.updateById(order);
    }
}
