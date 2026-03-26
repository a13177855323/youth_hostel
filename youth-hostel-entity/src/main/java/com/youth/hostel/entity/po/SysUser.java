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
@TableName("sys_user")
public class SysUser extends BaseEntity {

    private String username;

    private String password;

    private String nickname;

    private String realName;

    private String phone;

    private String email;

    private String avatar;

    private Integer gender;

    private String idCard;

    private LocalDate birthday;

    private Integer memberLevel;

    private Integer memberPoints;

    private BigDecimal balance;

    private Integer status;

    private LocalDateTime lastLoginTime;

    private String lastLoginIp;

    private String remark;
}
