package com.chris.interceptor;

import com.chris.context.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


@Component
public class ImageUploadInterceptor implements HandlerInterceptor {
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {

        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            // 只要带 token，就设置一个哨兵值，标记“已登录”
            UserContext.setCurrentId(-1L);
        } else {
            // 不带 token，标记为“未登录”
            UserContext.setCurrentId(null);
        }
        return true;
    }
}
