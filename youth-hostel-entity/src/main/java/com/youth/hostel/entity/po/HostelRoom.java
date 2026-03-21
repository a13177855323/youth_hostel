package com.youth.hostel.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.youth.hostel.entity.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hostel_room")
public class HostelRoom extends BaseEntity {

    private String roomNo;

    private String roomName;

    private String roomType;

    private String bedType;

    private Integer bedCount;

    private Integer floor;

    private BigDecimal area;

    private BigDecimal price;

    private BigDecimal deposit;

    private String facilities;

    private String images;

    private String description;

    private Integer maxOccupancy;

    private Integer hasWindow;

    private Integer hasBathroom;

    private Integer hasAircon;

    private Integer status;

    private Integer sortOrder;

    private String remark;
}
