package com.chris.vo.loginVOs;

import lombok.Data;

@Data
public class UserInfoVO {

    private String username;

    private String image;

    private String role;

    private String accessToken;

    private String refreshToken;

    private Boolean needsProfileCompletion; //Indicates whether the user needs to complete their profile
}
