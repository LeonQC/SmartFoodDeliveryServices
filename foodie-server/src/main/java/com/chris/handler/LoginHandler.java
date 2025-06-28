package com.chris.handler;

import com.chris.dto.LoginDTO;
import com.chris.enumeration.LoginType;
import com.chris.vo.UserInfoVO;

public interface LoginHandler {
    /**
     * Determines if this handler supports the given loginType.
     * @param loginType the login method enum (only PASSWORD or GOOGLE)
     * @return true if this handler can process the given loginType; false otherwise
     */
    boolean supports(LoginType loginType);

    /**
     * Executes the business logic for login and returns UserInfoVO.
     * @param loginDTO the DTO from the frontend, containing role, username, password, googleToken, etc.
     * @return the UserInfoVO to return to the frontend upon successful login
     */
    UserInfoVO handle(LoginDTO loginDTO);
}
