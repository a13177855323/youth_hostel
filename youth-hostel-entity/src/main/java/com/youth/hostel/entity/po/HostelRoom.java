package com.youth.hostel.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.youth.hostel.entity.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 青旅房源/房型实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hostel_room")
@Schema(description = "青旅房源/房型信息")
public class HostelRoom extends BaseEntity {

    @Schema(description = "房间编号")
    private String roomNo;

    @Schema(description = "房间名称")
    private String roomName;

    @Schema(description = "房型: SINGLE-单人间 DOUBLE-双人间 DORM-多人间 FAMILY-家庭房")
    private String roomType;

    @Schema(description = "床型: SINGLE_BED-单人床 DOUBLE_BED-双人床 BUNK_BED-上下铺")
    private String bedType;

    @Schema(description = "床位数")
    private Integer bedCount;

    @Schema(description = "所在楼层")
    private Integer floor;

    @Schema(description = "房间面积(平方米)")
    private BigDecimal area;

    @Schema(description = "房价(元/晚)")
    private BigDecimal price;

    @Schema(description = "押金(元)")
    private BigDecimal deposit;

    @Schema(description = "设施配置(JSON格式): WiFi、空调、独立卫浴等")
    private String facilities;

    @Schema(description = "房间图片(JSON数组)")
    private String images;

    @Schema(description = "房间描述")
    private String description;

    @Schema(description = "最大入住人数")
    private Integer maxOccupancy;

    @Schema(description = "是否有窗: 0-无 1-有")
    private Integer hasWindow;

    @Schema(description = "是否独立卫浴: 0-公共 1-独立")
    private Integer hasBathroom;

    @Schema(description = "是否有空调: 0-无 1-有")
    private Integer hasAircon;

    @Schema(description = "状态: 0-维护中 1-可预订 2-已入住")
    private Integer status;

    @Schema(description = "排序号")
    private Integer sortOrder;

    @Schema(description = "备注")
    private String remark;
}
