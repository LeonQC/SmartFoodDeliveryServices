package com.chris.dto;

import com.chris.enumeration.RoleType;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * Registration DTO 中包含的通用字段
 */
@Data
public class UserDTO {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @Email @NotBlank
    private String email;

    @NotBlank
    private String phone;

    @NotNull
    private RoleType role;
}
