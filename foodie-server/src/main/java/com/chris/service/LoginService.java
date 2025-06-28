package com.chris.service;

import com.chris.dto.LoginDTO;
import com.chris.enumeration.RoleType;
import com.chris.vo.UserInfoVO;

public interface LoginService {
    UserInfoVO loginByPassword(RoleType role,  String username, String password);

    UserInfoVO loginByOAuth2(RoleType role, String token);
}
