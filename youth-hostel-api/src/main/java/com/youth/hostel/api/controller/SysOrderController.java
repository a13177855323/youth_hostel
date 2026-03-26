package com.youth.hostel.api.controller;

import com.youth.hostel.common.annotation.RequiresPermission;
import com.youth.hostel.common.context.UserContextHolder;
import com.youth.hostel.common.result.Result;
import com.youth.hostel.entity.dto.OrderCreateDTO;
import com.youth.hostel.service.SysOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Tag(name = "订单管理")
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@RequiresPermission  // 类级别的权限注解，所有接口需要登录
public class SysOrderController {

    private final SysOrderService orderService;

    @Operation(summary = "创建订单")
    @PostMapping("/create")
    public Result<String> createOrder(@RequestBody OrderCreateDTO dto) {
        // 使用当前登录用户ID创建订单（防止用户指定其他用户ID）
        Long currentUserId = UserContextHolder.getUserId();
        // 金额也需要校验，不能直接信任前端传入
        if (dto.getTotalAmount() == null || dto.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return Result.failed("订单金额必须大于0");
        }
        String orderNo = orderService.createOrder(currentUserId, dto.getTotalAmount());
        return Result.success(orderNo);
    }

    @Operation(summary = "支付订单")
    @PostMapping("/pay")
    public Result<Void> payOrder(@RequestParam String orderNo) {
        // 支付订单时也需要校验订单所属用户是否为当前登录用户
        // 这里简化处理，具体校验逻辑在service层实现
        orderService.payOrder(orderNo);
        return Result.success();
    }
}
