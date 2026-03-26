package com.youth.hostel.api.controller;

import com.youth.hostel.common.exception.BusinessException;
import com.youth.hostel.common.result.Result;
import com.youth.hostel.entity.dto.OrderCreateDTO;
import com.youth.hostel.service.SysOrderService;
import com.youth.hostel.util.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "订单管理")
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class SysOrderController {

    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    private final SysOrderService orderService;

    @Operation(summary = "创建订单")
    @PostMapping("/create")
    public Result<String> createOrder(@RequestBody OrderCreateDTO dto) {
        Long currentUserId = UserContext.getUserId();
        if (currentUserId == null) {
            throw new BusinessException("未登录");
        }

        // 权限检查：普通用户只能为自己创建订单，管理员可以为指定用户创建
        String currentRoleCode = UserContext.getRoleCode();
        if (!ROLE_ADMIN.equals(currentRoleCode) && !currentUserId.equals(dto.getUserId())) {
            throw new BusinessException("无权限：只能为自己创建订单");
        }

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
