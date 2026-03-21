package com.youth.hostel.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.youth.hostel.entity.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 预定订单实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("booking_order")
@Schema(description = "预定订单信息")
public class BookingOrder extends BaseEntity {

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "房源ID")
    private Long roomId;

    @Schema(description = "房间编号(冗余)")
    private String roomNo;

    @Schema(description = "房间名称(冗余)")
    private String roomName;

    @Schema(description = "房型(冗余)")
    private String roomType;

    @Schema(description = "入住日期")
    private LocalDate checkInDate;

    @Schema(description = "离店日期")
    private LocalDate checkOutDate;

    @Schema(description = "入住天数")
    private Integer stayDays;

    @Schema(description = "入住人姓名")
    private String guestName;

    @Schema(description = "入住人电话")
    private String guestPhone;

    @Schema(description = "入住人身份证")
    private String guestIdCard;

    @Schema(description = "入住人数")
    private Integer guestCount;

    @Schema(description = "房间单价(元/晚)")
    private BigDecimal roomPrice;

    @Schema(description = "订单总金额")
    private BigDecimal totalAmount;

    @Schema(description = "押金")
    private BigDecimal deposit;

    @Schema(description = "优惠金额")
    private BigDecimal discountAmount;

    @Schema(description = "实付金额")
    private BigDecimal payAmount;

    @Schema(description = "支付方式: 1-微信 2-支付宝 3-余额 4-到店付")
    private Integer payType;

    @Schema(description = "支付时间")
    private LocalDateTime payTime;

    @Schema(description = "第三方支付流水号")
    private String payTradeNo;

    @Schema(description = "订单状态: 0-待支付 1-已支付 2-已入住 3-已完成 4-已取消 5-已退款")
    private Integer orderStatus;

    @Schema(description = "实际入住时间")
    private LocalDateTime checkInTime;

    @Schema(description = "实际离店时间")
    private LocalDateTime checkOutTime;

    @Schema(description = "取消原因")
    private String cancelReason;

    @Schema(description = "取消时间")
    private LocalDateTime cancelTime;

    @Schema(description = "退款金额")
    private BigDecimal refundAmount;

    @Schema(description = "订单备注")
    private String remark;
}
