package com.chris.interceptor;

import com.chris.context.UserContext;
import com.chris.exception.ProfileIncompleteException;
import com.chris.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.chris.constant.MessageConstant.USER_PROFILE_INCOMPLETE;

@Component
public class ProfileCompletedInterceptor implements HandlerInterceptor {

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {
        Long userId = UserContext.getCurrentId();
        userRepository.findById(userId).ifPresent(user -> {
            if (!user.getProfileCompleted()) {
                throw new ProfileIncompleteException(USER_PROFILE_INCOMPLETE);
            }
        });
        return true;
    }
}
