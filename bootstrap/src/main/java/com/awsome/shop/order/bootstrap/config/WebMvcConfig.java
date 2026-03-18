package com.awsome.shop.order.bootstrap.config;

import com.awsome.shop.order.repository.mysql.config.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 *
 * <p>注册拦截器，从请求头中提取用户信息并设置到 UserContext，
 * 供审计字段（createdBy/updatedBy）自动填充使用。</p>
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserContextInterceptor())
                .addPathPatterns("/api/**");
    }

    /**
     * 用户上下文拦截器 — 从 X-Operator-Id 请求头提取用户ID
     */
    private static class UserContextInterceptor implements HandlerInterceptor {

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
            String operatorId = request.getHeader("X-Operator-Id");
            if (operatorId != null && !operatorId.isBlank()) {
                try {
                    UserContext.setCurrentUserId(Long.parseLong(operatorId));
                } catch (NumberFormatException ignored) {
                    // 非法格式忽略，后续 Controller 层会校验
                }
            }
            return true;
        }

        @Override
        public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                    Object handler, Exception ex) {
            UserContext.clear();
        }
    }
}
