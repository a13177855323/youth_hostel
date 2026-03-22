package com.youth.hostel.api.controller;

import com.youth.hostel.common.result.Result;
import com.youth.hostel.entity.dto.OrderCreateDTO;
import com.youth.hostel.service.SysOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "订单管理")
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class SysOrderController {

    private final SysOrderService orderService;

    @Operation(summary = "创建订单")
    @PostMapping("/create")
    public Result<String> createOrder(@RequestBody OrderCreateDTO dto) {
        String orderNo = orderService.createOrder(dto.getUserId(), dto.getTotalAmount());
        return Result.success(orderNo);
    }

    @Operation(summary = "支付订单")
    @PostMapping("/pay")
    public Result<Void> payOrder(@RequestParam String orderNo) {
        orderService.payOrder(orderNo);
        return Result.success();
    }
}
