package com.chris.aop;

import com.chris.annotation.RequireProfileComplete;
import com.chris.context.UserContext;
import com.chris.entity.User;
import com.chris.exception.ProfileIncompleteException;
import com.chris.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import static com.chris.constant.MessageConstant.USER_PROFILE_INCOMPLETE;

@Aspect
@Component
@RequiredArgsConstructor
public class ProfileCheckAspect {

    private final UserRepository userRepository;

    // 切点：所有标了 @RequireProfileComplete 的方法
    @Pointcut("@annotation(requireProfileComplete)")
    public void requireProfile(RequireProfileComplete requireProfileComplete) {}

    @Before(value = "requireProfile(requireProfileComplete)", argNames = "requireProfileComplete")
    public void checkProfile(RequireProfileComplete requireProfileComplete) {
        Long userId = UserContext.getCurrentId();
        boolean ok = userRepository.findById(userId)
                .map(User::getProfileCompleted)
                .orElse(false);
        if (!ok) {
            throw new ProfileIncompleteException(USER_PROFILE_INCOMPLETE);
        }
    }
}
