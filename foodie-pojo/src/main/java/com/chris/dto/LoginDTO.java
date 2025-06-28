package com.chris.dto;

import com.chris.enumeration.LoginType;
import com.chris.enumeration.RoleType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

/**
 * 通用登录 DTO，根据 loginType 不同触发不同的LoginHandler。
 * OAuth2.0 验证为新用户时，自动注册。根据 email 创建 UNIQUE username, 并与 role 一起写入 DB
 */
@Data
public class LoginDTO {

    @NotNull(message = "loginType 不能为空")
    private LoginType loginType;

    @NotNull(message = "role 不能为空")
    private RoleType role;

    private String username;

    private String password;

    private String oauth2Token;


    /**
     * When loginType = "password" ，username && password cannot be null
     */
    @AssertTrue(message = "username and password are required when loginType is \"password\"")
    private boolean isPasswordFieldsPresent() {
        if (LoginType.PASSWORD.equals(loginType)) {
            return username != null && !username.isBlank()
                    && password != null && !password.isBlank();
        }
        return true;
    }

    /**
     * When loginType = "google" ，googleToken cannot be null
     */
    @AssertTrue(message = "googleToken is required when loginType is \"google\"")
    private boolean isGoogleTokenPresent() {
        if (LoginType.OAUTH2.equals(loginType)) {
            return oauth2Token != null && !oauth2Token.isBlank();
        }
        return true;
    }
}
