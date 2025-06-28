package com.chris.dto;

import com.chris.dto.groups.ClientGroup;
import com.chris.dto.groups.MerchantGroup;
import com.chris.dto.groups.RiderGroup;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Map;

/**
 * 更新 Profile 时传入的 DTO
 * 与 RegistrationDTO 不同的是：通用字段中，只有 phone 字段可修改
 * 注意：User中的 profileCompleted 字段需设为true (通过MapStruct完成). Merchant 中还需根据经纬度计算 geometry 并添加到 location 字段中
 */
@Data
public class ProfileUpdateDTO {

    /** 通用可修改字段 */
    @NotBlank
    private String phone;

    // —— Merchant 专属字段 ——
    @NotBlank(groups = MerchantGroup.class)
    private String address;

    @NotBlank(groups = MerchantGroup.class)
    private String city;

    @NotBlank(groups = MerchantGroup.class)
    private String state;

    @NotBlank(groups = MerchantGroup.class)
    private String country;

    @NotBlank(groups = MerchantGroup.class)
    private String merchantName;

    // 以下为可选字段，不强制校验
    private String zipcode;
    private String merchantDescription;
    private String merchantImage;
    private String merchantType;
    private String merchantSocialMedia;

    @NotEmpty(groups = MerchantGroup.class)
    private Map<String,String> merchantOpeningHours;

    /** Rider & Client 专属字段 ↓ */
    @NotBlank(groups = {RiderGroup.class, ClientGroup.class})
    @Pattern(
            groups = {RiderGroup.class, ClientGroup.class},
            regexp = "0|1",
            message = "性别只能为 '0'(女性) 或 '1'(男性)"
    )
    private String gender;

    // 头像可选
    private String avatar;
}
