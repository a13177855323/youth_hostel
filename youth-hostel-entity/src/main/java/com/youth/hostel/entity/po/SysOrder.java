package com.youth.hostel.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.youth.hostel.entity.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_order")
public class SysOrder extends BaseEntity {

    private String orderNo;

    private Long userId;

    private BigDecimal totalAmount;

    private Integer status;

    private LocalDateTime payTime;
}
