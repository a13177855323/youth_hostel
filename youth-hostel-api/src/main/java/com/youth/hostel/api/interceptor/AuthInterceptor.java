package com.youth.hostel.api.interceptor;

import com.youth.hostel.common.exception.BusinessException;
import com.youth.hostel.util.JwtUtil;
import com.youth.hostel.util.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 登录接口不需要认证
        String requestUri = request.getRequestURI();
        if (requestUri.contains("/api/user/login")) {
            return true;
        }

        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            throw new BusinessException("未登录或登录已过期");
        }

        String token = authHeader.substring(BEARER_PREFIX.length());
        if (!JwtUtil.validateToken(token)) {
            throw new BusinessException("登录已过期，请重新登录");
        }

        // 将用户信息存入上下文
        Long userId = JwtUtil.getUserId(token);
        String username = JwtUtil.getUsername(token);
        String roleCode = JwtUtil.getRoleCode(token);

        UserContext.setUserId(userId);
        UserContext.setUsername(username);
        UserContext.setRoleCode(roleCode);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
}
