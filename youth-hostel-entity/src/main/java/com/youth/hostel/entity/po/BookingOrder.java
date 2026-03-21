package com.youth.hostel.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.youth.hostel.entity.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("booking_order")
public class BookingOrder extends BaseEntity {

    private String orderNo;

    private Long userId;

    private Long roomId;

    private String roomNo;

    private String roomName;

    private String roomType;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private Integer stayDays;

    private String guestName;

    private String guestPhone;

    private String guestIdCard;

    private Integer guestCount;

    private BigDecimal roomPrice;

    private BigDecimal totalAmount;

    private BigDecimal deposit;

    private BigDecimal discountAmount;

    private BigDecimal payAmount;

    private Integer payType;

    private LocalDateTime payTime;

    private String payTradeNo;

    private Integer orderStatus;

    private LocalDateTime checkInTime;

    private LocalDateTime checkOutTime;

    private String cancelReason;

    private LocalDateTime cancelTime;

    private BigDecimal refundAmount;

    private String remark;
}
