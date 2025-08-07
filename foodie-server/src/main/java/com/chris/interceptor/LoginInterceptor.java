package com.chris.interceptor;

import com.chris.context.UserContext;
import com.chris.exception.InvalidTokenException;
import com.chris.exception.MissingTokenException;
import com.chris.utils.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

import static com.chris.constant.InterceptorConstant.HEADER_AUTH;
import static com.chris.constant.InterceptorConstant.HEADER_REFRESH;
import static com.chris.constant.JwtClaimsConstant.ROLE;
import static com.chris.constant.JwtClaimsConstant.USER_ID;
import static com.chris.constant.MessageConstant.*;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {
        // —— 1. 验证 AccessToken ——
        String accessHeader = request.getHeader(HEADER_AUTH);
        if (accessHeader == null || !accessHeader.startsWith("Bearer ")) {
            throw new MissingTokenException(ACCESS_TOKEN_MISSING);
        }
        String accessToken = accessHeader.substring(7);

        Long userId;
        try {
            Claims accessClaims = jwtTokenUtil.parseAccessToken(accessToken);
            userId = accessClaims.get(USER_ID, Long.class);
        } catch (InvalidTokenException e) {
            // 如果 AccessToken 过期/非法，就走刷新逻辑
            userId = tryRefreshAndGetUserId(request, response, e.getMessage());
        }

        // —— 2. 把 userId 放入 ThreadLocal ——
        UserContext.setCurrentId(userId);

        return true;
    }

    private Long tryRefreshAndGetUserId(HttpServletRequest request,
                                        HttpServletResponse response,
                                        String originalMsg) {
        String refreshHeader = request.getHeader(HEADER_REFRESH);
        if (refreshHeader == null || !refreshHeader.startsWith("Bearer ")) {
            throw new MissingTokenException(REFRESH_TOKEN_MISSING);
        }
        String refreshToken = refreshHeader.substring(7);

        try {
            Claims refreshClaims = jwtTokenUtil.parseRefreshToken(refreshToken);
            Long userId = refreshClaims.get(USER_ID, Long.class);
            String role   = refreshClaims.get(ROLE, String.class);

            // 生成新的 AccessToken
            Map<String,Object> newClaims = Map.of(
                    USER_ID, userId,
                    ROLE,    role
            );
            String newAt = jwtTokenUtil.generateAccessToken(newClaims);
            response.setHeader(HEADER_AUTH, "Bearer " + newAt);

            return userId;
        } catch (InvalidTokenException e) {
            throw new InvalidTokenException("[" + originalMsg + "] and [" + e.getMessage() + "]");
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {
        UserContext.remove();
    }
}

