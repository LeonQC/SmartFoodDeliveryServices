package com.chris.dto;

import lombok.Data;

/**
 * OAuth2.0 token中包含的常用信息
 */
@Data
public class OAuth2UserInfo {
    private String sub;           // OAuth2.0 user's unique ID
    private String email;         // OAuth2.0 account email
    private boolean emailVerified; // Whether the email has been verified
    private String name;          // User's name as registered with OAuth2.0
    private String picture;       // URL of the user's profile picture
    private String locale;        // Language/region
}
