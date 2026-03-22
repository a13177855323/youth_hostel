package com.youth.hostel.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.youth.hostel.entity.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_wallet")
public class SysWallet extends BaseEntity {

    private Long userId;

    private BigDecimal balance;

    @Version
    private Integer version;
}
