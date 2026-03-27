package com.youth.hostel.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.youth.hostel.entity.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_sign_record")
public class SysSignRecord extends BaseEntity {

    private Long userId;

    private LocalDate signDate;

    private Integer rewardPoints;
}
