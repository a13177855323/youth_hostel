package com.youth.hostel.common.annotation;

import java.lang.annotation.*;

/**
 * 权限校验注解
 * 标记需要特定权限才能访问的接口
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresPermission {

    /**
     * 需要的权限编码
     * 特殊值："ROLE_ADMIN" 表示需要管理员角色
     */
    String value() default "";

    /**
     * 是否需要登录（默认需要）
     */
    boolean requireLogin() default true;

    /**
     * 是否需要管理员权限
     */
    boolean requireAdmin() default false;
}
