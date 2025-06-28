package com.chris.mapper;

import com.chris.dto.RegistrationDTO;
import com.chris.entity.Client;
import com.chris.entity.Merchant;
import com.chris.entity.Rider;
import com.chris.entity.User;
import org.mapstruct.*;

// 1. 定义 Mapper 接口
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RegistrationMapper {

    // 把 DTO 的通用字段 user.*  映射到 User 实体
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.password", target = "password")         // 你也可以在这里加 @Named 方法做密码加密
    @Mapping(source = "user.email",    target = "email")
    @Mapping(source = "user.role",     target = "role")
    @Mapping(constant = "true",        target = "profileCompleted") // 注册可确保账号已完善
    User toUser(RegistrationDTO dto);

    // Merchant 专属映射
    @Mapping(source = "user.phone",           target = "phone")
    @Mapping(source = "address",              target = "address")
    @Mapping(source = "city",                 target = "city")
    @Mapping(source = "state",                target = "state")
    @Mapping(source = "country",              target = "country")
    @Mapping(source = "merchantName",         target = "merchantName")
    @Mapping(source = "zipcode",              target = "zipcode")
    @Mapping(source = "merchantDescription",  target = "merchantDescription")
    @Mapping(source = "merchantImage",        target = "merchantImage")
    @Mapping(source = "merchantType",         target = "merchantType")
    @Mapping(source = "merchantSocialMedia",  target = "merchantSocialMedia")
    @Mapping(source = "merchantOpeningHours", target = "merchantOpeningHours")
    @Mapping(constant = "0", target = "merchantStatus")
    Merchant toMerchant(RegistrationDTO dto);

    // Client 专属映射
    @Mapping(source = "user.phone",target = "phone")
    @Mapping(source = "gender", target = "gender")
    @Mapping(source = "avatar", target = "avatar")
    Client toClient(RegistrationDTO dto);

    // Rider 专属映射
    @Mapping(source = "user.phone",target = "phone")
    @Mapping(source = "gender", target = "gender")
    @Mapping(source = "avatar", target = "avatar")
    @Mapping(constant = "0", target = "riderStatus")
    Rider toRider(RegistrationDTO dto);

    // After‐mapping：注入双向关联
    @AfterMapping
    default void linkUserAndChild(RegistrationDTO dto,
                                  @MappingTarget User user) {
        switch (dto.getUser().getRole()) {
            case MERCHANT -> {
                Merchant m = toMerchant(dto);
                m.setUser(user);
                user.setMerchant(m);
            }
            case CLIENT -> {
                Client c = toClient(dto);
                c.setUser(user);
                user.setClient(c);
            }
            case RIDER -> {
                Rider r = toRider(dto);
                r.setUser(user);
                user.setRider(r);
            }
        }
    }
}

