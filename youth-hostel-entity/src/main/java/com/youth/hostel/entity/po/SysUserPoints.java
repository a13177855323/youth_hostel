package com.youth.hostel.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.youth.hostel.entity.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user_points")
public class SysUserPoints extends BaseEntity {

    private Long userId;

    private Integer totalPoints;

    private Integer availablePoints;
}
