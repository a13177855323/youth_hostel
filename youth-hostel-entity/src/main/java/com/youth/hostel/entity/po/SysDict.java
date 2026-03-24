package com.youth.hostel.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.youth.hostel.entity.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 基础字典实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dict")
@Schema(description = "基础字典信息")
public class SysDict extends BaseEntity {

    @Schema(description = "字典类型编码")
    private String dictType;

    @Schema(description = "字典标签(显示值)")
    private String dictLabel;

    @Schema(description = "字典值(存储值)")
    private String dictValue;

    @Schema(description = "排序号")
    private Integer dictSort;

    @Schema(description = "CSS样式")
    private String cssClass;

    @Schema(description = "表格回显样式")
    private String listClass;

    @Schema(description = "是否默认: 0-否 1-是")
    private Integer isDefault;

    @Schema(description = "状态: 0-禁用 1-正常")
    private Integer status;

    @Schema(description = "备注")
    private String remark;
}
