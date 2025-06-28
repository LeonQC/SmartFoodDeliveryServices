package com.chris.controller.account;

import com.chris.dto.LoginDTO;
import com.chris.enumeration.LoginType;
import com.chris.handler.LoginHandler;
import com.chris.vo.resultVOs.Result;
import com.chris.vo.loginVOs.UserInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/login")
@Tag(name = "LoginController", description = "Login-related APIs")
public class LoginController {

    private final List<LoginHandler> handlers;

    /**
     * Spring will automatically inject all Beans that implement the LoginHandler interface into this list
     */
    @Autowired
    public LoginController(List<LoginHandler> handlers) {
        this.handlers = handlers;
    }

    @PostMapping
    @Operation(summary = "User Login", description = "Performs login via username/password or OAuth2.0 token")
    public Result<UserInfoVO> login(@RequestBody @Valid LoginDTO loginDTO) {
        // 1. 将前端传过来的 loginType 字符串转换为枚举
        LoginType loginType = loginDTO.getLoginType();
        if (loginType == null) {
            //  防止意外出现 loginType 不在枚举中的情况
            throw new IllegalStateException("Unsupported loginType");
        }

        // 2. 在所有 LoginHandler 实现中，找到 supports(loginType) 返回 true 的那一个，然后调用它
        return handlers.stream()
                .filter(h -> h.supports(loginType))
                .findFirst()
                .map(handler -> Result.success(handler.handle(loginDTO)))
                .orElseThrow(() -> new IllegalStateException(String.format("No handler found for loginType: %s", loginType)));
    }
}
