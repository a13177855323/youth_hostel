package com.youth.hostel.api.interceptor;

import com.youth.hostel.common.annotation.RequiresPermission;
import com.youth.hostel.common.context.UserContextHolder;
import com.youth.hostel.common.exception.BusinessException;
import com.youth.hostel.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * 权限拦截器
 * 用于接口级别的权限校验
 */
@Component
@RequiredArgsConstructor
public class PermissionInterceptor implements HandlerInterceptor {

    private final SysRoleService sysRoleService;

    /**
     * 请求头中携带的用户ID（简化实现，实际应从Token中解析）
     */
    private static final String HEADER_USER_ID = "X-User-Id";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 清除之前的上下文（防止内存泄漏）
        UserContextHolder.clear();

        // 如果不是映射到方法的请求，直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        Class<?> clazz = method.getDeclaringClass();

        // 获取权限注解（先检查方法，再检查类）
        RequiresPermission permissionAnnotation = getRequiresPermissionAnnotation(method, clazz);

        // 如果没有权限注解，直接通过（或根据需求设置默认需要登录）
        if (permissionAnnotation == null) {
            return true;
        }

        // 如果需要登录
        if (permissionAnnotation.requireLogin()) {
            Long currentUserId = getCurrentUserId(request);
            if (currentUserId == null) {
                throw new BusinessException("请先登录");
            }
            // 将用户ID存入上下文
            UserContextHolder.setUserId(currentUserId);

            // 如果需要管理员权限
            if (permissionAnnotation.requireAdmin()) {
                sysRoleService.checkPermission(currentUserId, "ROLE_ADMIN");
            }
            // 如果需要特定权限
            else if (!permissionAnnotation.value().isEmpty()) {
                sysRoleService.checkPermission(currentUserId, permissionAnnotation.value());
            }
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 请求完成后清除上下文
        UserContextHolder.clear();
    }

    /**
     * 获取权限注解（方法级优先于类级）
     */
    private RequiresPermission getRequiresPermissionAnnotation(Method method, Class<?> clazz) {
        // 先检查方法上的注解
        if (method.isAnnotationPresent(RequiresPermission.class)) {
            return method.getAnnotation(RequiresPermission.class);
        }
        // 再检查类上的注解
        if (clazz.isAnnotationPresent(RequiresPermission.class)) {
            return clazz.getAnnotation(RequiresPermission.class);
        }
        return null;
    }

    /**
     * 从请求头获取当前用户ID（简化实现，实际应从Token解析）
     */
    private Long getCurrentUserId(HttpServletRequest request) {
        String userIdStr = request.getHeader(HEADER_USER_ID);
        if (userIdStr != null && !userIdStr.trim().isEmpty()) {
            try {
                return Long.parseLong(userIdStr);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
