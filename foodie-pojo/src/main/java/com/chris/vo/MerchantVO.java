package com.chris.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MerchantVO {
    private Long merchantId;
    private String phone;
    private String address;
    private String city;
    private String state;
    private String zipcode;
    private String country;
    private Double longitude;
    private Double latitude;
    private String merchantName;
    private String merchantDescription;
    private String merchantImage;
    private String merchantType;
    private String merchantSocialMedia;
    private Map<String, String> merchantOpeningHours;
}
