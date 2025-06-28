package com.chris.service.impl;

import com.chris.dto.OAuth2UserInfo;
import com.chris.entity.Client;
import com.chris.entity.Merchant;
import com.chris.entity.Rider;
import com.chris.entity.User;
import com.chris.enumeration.RoleType;
import com.chris.exception.AccountNotFoundException;
import com.chris.exception.AccountPasswordMismatchException;
import com.chris.exception.InvalidGoogleOAuthException;
import com.chris.repository.UserRepository;
import com.chris.service.GoogleOAuthService;
import com.chris.service.LoginService;
import com.chris.utils.JwtTokenUtil;
import com.chris.vo.UserInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.chris.constant.JwtClaimsConstant.ROLE;
import static com.chris.constant.JwtClaimsConstant.USER_ID;
import static com.chris.constant.MessageConstant.*;
import static com.chris.utils.GeoUtil.ORIGIN;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GoogleOAuthService googleOAuthService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    /**
     * Login by username/password
     * @param username
     * @param password
     * @param role
     * @return
     */
    @Override
    public UserInfoVO loginByPassword(RoleType role, String username, String password) {
        // 1. 找到用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AccountNotFoundException(ACCOUNT_NOT_FOUND));
        // 2. 校验角色
        if (user.getRole() != role) {
            throw new IllegalStateException(INCORRECT_USER_ROLE);
        }
        // 3. 校验密码
        if (!password.equals(user.getPassword())) {
            throw new AccountPasswordMismatchException(ACCOUNT_PASSWORD_MISMATCH);
        }
        // 4. 生成并返回 VO
        return buildUserInfoVO(user);
    }

    /**
     * Login by OAuth2.0 token
     * @param token
     * @param role
     * @return
     */
    @Override
    public UserInfoVO loginByOAuth2(RoleType role, String token) {
        // 1. 验证 Google Token
        OAuth2UserInfo info = googleOAuthService.validateToken(token);
        String email = info.getEmail();
        if (email == null) {
            throw new InvalidGoogleOAuthException(OAUTH_TOKEN_INVALID);
        }
        // 2. 查库或注册
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> registerNewUser(email, role));
        // 3. 校验角色
        if (user.getRole() != role) {
            throw new IllegalStateException(INCORRECT_USER_ROLE);
        }
        // 4. 返回 VO（含 token）
        return buildUserInfoVO(user);
    }

    /**
     * Private method: generates accessToken and refreshToken based on a Merchant instance and role, then wraps them into a UserInfoVO
     */
    private UserInfoVO buildUserInfoVO(User user) {
        // 组装 JWT Claims
        Map<String, Object> claims = Map.of(
                USER_ID, user.getUserId(),
                ROLE,    user.getRole().name()
        );
        String at = jwtTokenUtil.generateAccessToken(claims);
        String rt = jwtTokenUtil.generateRefreshToken(claims);

        // 填充返回 VO
        UserInfoVO vo = new UserInfoVO();
        vo.setUsername(user.getUsername());
        vo.setEmail(user.getEmail());
        vo.setRole(user.getRole().name());
        vo.setAccessToken(at);
        vo.setRefreshToken(rt);
        vo.setNeedsProfileCompletion(!user.getProfileCompleted());
        return vo;
    }

    /**
     * Automatically register a new Merchant. All NOT NULL columns must be assigned a valid default value,
     * such as an empty string, 0, false, etc.
     * If additional information needs to be completed later, the frontend can be directed to a “profile” page after login.
     */
    private User registerNewUser(String email, RoleType role) {
        // 自动注册一个最简占位 User+子表
        User user = new User();
        String base = email.split("@")[0];
        user.setUsername(generateUniqueUsername(base));
        user.setEmail(email);
        user.setPassword("");              // 或设置随机密码
        user.setRole(role);
        user.setProfileCompleted(false);

        // 根据角色创建子表实体
        switch (role) {
            case MERCHANT -> {
                Merchant m = new Merchant();
                // 以下字段由于表结构是 NOT NULL，只能给“临时”或“占位”值
                m.setPhone("");
                m.setAddress("");
                m.setCity("");
                m.setState("");
                m.setCountry("");
                m.setLongitude(0.0);
                m.setLatitude(0.0);
                m.setMerchantName("");
                m.setMerchantOpeningHours(Map.of());
                m.setMerchantStatus((short) 0);
                m.setLocation(ORIGIN);

                m.setUser(user);
                user.setMerchant(m);
            }
            case CLIENT -> {
                Client c = new Client();
                c.setPhone("");
                c.setGender("");

                c.setUser(user);
                user.setClient(c);
            }
            case RIDER -> {
                Rider r = new Rider();
                r.setPhone("");
                r.setGender("");

                r.setUser(user);
                user.setRider(r);
            }
        }

        return userRepository.save(user);
    }
    /**
     * Private method: generates unique username based on base for email login users
     */
    private String generateUniqueUsername(String base) {
        String name = base;
        int idx = 0;
        while (userRepository.findByUsername(name).isPresent()) {
            name = base + (++idx);
        }
        return name;
    }
}
