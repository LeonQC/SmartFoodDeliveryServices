package com.chris.config;

import com.chris.interceptor.AuthInterceptor;
import com.chris.json.JacksonObjectMapper;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@Slf4j
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    /**
     * 1. 注册拦截器，同时放行文档相关接口和静态资源
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                // 只拦 /merchants/**、/clients/**、/riders/** 这三类业务路径
                .addPathPatterns("/merchants/**", "/clients/**", "/riders/**", "/profile/**")
                // 放行：
                //  - 你自己业务中的子路径
                //  - Knife4j UI 页面
                //  - Springdoc/Swagger UI 静态资源
                .excludePathPatterns(
                        "/clients/shops/**"
                );
    }
}
