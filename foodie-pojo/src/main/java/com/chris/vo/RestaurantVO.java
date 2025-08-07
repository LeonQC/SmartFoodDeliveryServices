package com.chris.vo;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestaurantVO {
    private Long merchantId;
    private String merchantName;
    private String phone;
    private String address;
    private String city;
    private String state;
    private String merchantDescription;
    private String merchantImage;
    private String merchantType;
    private String merchantSocialMedia;
    private Map<String, String> merchantOpeningHours;
    private String merchantStatus;

    private List<CategoryVO>  categories;
}
