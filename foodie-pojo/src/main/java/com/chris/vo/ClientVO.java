package com.chris.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClientVO {
    private Long clientId;
    private String phone;
    private String gender;  // "0": female, "1": male
    private String avatar;
}
