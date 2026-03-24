package com.youth.hostel.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.youth.hostel.entity.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户/会员实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
@Schema(description = "用户/会员信息")
public class SysUser extends BaseEntity {

    @Schema(description = "用户名/登录账号")
    private String username;

    @Schema(description = "密码(加密存储)")
    private String password;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "真实姓名")
    private String realName;

    @Schema(description = "手机号码")
    private String phone;

    @Schema(description = "邮箱地址")
    private String email;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "性别: 0-未知 1-男 2-女")
    private Integer gender;

    @Schema(description = "身份证号")
    private String idCard;

    @Schema(description = "出生日期")
    private LocalDate birthday;

    @Schema(description = "会员等级: 0-普通 1-银卡 2-金卡 3-白金")
    private Integer memberLevel;

    @Schema(description = "会员积分")
    private Integer memberPoints;

    @Schema(description = "账户余额")
    private BigDecimal balance;

    @Schema(description = "状态: 0-禁用 1-正常")
    private Integer status;

    @Schema(description = "最后登录时间")
    private LocalDateTime lastLoginTime;

    @Schema(description = "最后登录IP")
    private String lastLoginIp;

    @Schema(description = "备注")
    private String remark;
}
