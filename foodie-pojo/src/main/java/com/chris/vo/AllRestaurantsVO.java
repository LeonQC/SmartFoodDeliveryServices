package com.chris.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AllRestaurantsVO {
    private Long merchantId;
    private String merchantName;
    private String address;
    private String merchantDescription;
    private String merchantImage;
    private String merchantType;
    private Short merchantStatus;

    /** 用于距离排序的字段 */
    private Double distance;
}
