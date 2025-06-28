package com.chris.config;

import com.chris.interceptor.LoginInterceptor;
import com.chris.interceptor.ProfileCompletedInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private LoginInterceptor loginInterceptor;
    @Autowired
    private ProfileCompletedInterceptor profileCompletedInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 登录校验，拦所有需要登录的接口
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/client/**", "/merchant/**", "/rider/**", "/profile/**", "/upload/**")
                .order(0);

        // 资料完善校验，不拦 profile 补全接口
        registry.addInterceptor(profileCompletedInterceptor)
                .addPathPatterns("/client/**", "/merchant/**", "/rider/**")
                .excludePathPatterns("/profile/**")
                .order(1);
    }
}
