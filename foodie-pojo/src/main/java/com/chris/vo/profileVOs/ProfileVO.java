package com.chris.vo.profileVOs;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileVO {
    private String username;
    private String email;
    private String role;
    private Boolean profileCompleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private MerchantVO merchant;
    private ClientVO client;
    private RiderVO rider;
}
