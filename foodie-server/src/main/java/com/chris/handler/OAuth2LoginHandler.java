package com.chris.handler;

import com.chris.enumeration.LoginType;
import com.chris.dto.LoginDTO;
import com.chris.service.*;
import com.chris.vo.UserInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OAuth2LoginHandler implements LoginHandler {

    @Autowired
    private LoginService loginService;

    @Override
    public boolean supports(LoginType loginType) {
        return loginType == LoginType.OAUTH2;
    }

    @Override
    public UserInfoVO handle(LoginDTO loginDTO) {

        return loginService.loginByOAuth2(loginDTO.getRole(), loginDTO.getOauth2Token());
    }
}
