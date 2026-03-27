package com.youth.hostel.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.youth.hostel.entity.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUser extends BaseEntity {

    private String username;

    private String password;

    private String nickname;

    private String phone;

    private String email;

    private Integer status;

    /**
     * 会员积分
     */
    private Integer memberPoints;

    /**
     * 账户余额
     */
    private BigDecimal balance;
}
