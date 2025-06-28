package com.chris.handler;

import com.chris.enumeration.LoginType;
import com.chris.dto.LoginDTO;
import com.chris.service.LoginService;
import com.chris.vo.loginVOs.UserInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PasswordLoginHandler implements LoginHandler {

    @Autowired
    private LoginService  loginService;

    @Override
    public boolean supports(LoginType loginType) {
        return loginType == LoginType.PASSWORD;
    }

    @Override
    public UserInfoVO handle(LoginDTO loginDTO) {

        return loginService.loginByPassword(loginDTO.getRole(),  loginDTO.getUsername(), loginDTO.getPassword());
    }
}
