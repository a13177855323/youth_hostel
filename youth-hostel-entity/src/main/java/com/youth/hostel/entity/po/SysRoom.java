package com.youth.hostel.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.youth.hostel.entity.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_room")
public class SysRoom extends BaseEntity {

    private String roomNumber;

    private String roomType;

    private Integer capacity;

    private BigDecimal price;

    private Integer stock;

    private String description;

    private Integer status;

    @Version
    private Integer version;
}
