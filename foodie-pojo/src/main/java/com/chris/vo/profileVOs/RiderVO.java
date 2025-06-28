package com.chris.vo.profileVOs;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RiderVO {
    private Long riderId;
    private String phone;
    private String gender;  // "0": female, "1": male
    private String avatar;
}
