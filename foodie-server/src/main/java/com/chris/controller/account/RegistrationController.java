package com.chris.controller.account;

import com.chris.dto.RegistrationDTO;
import com.chris.dto.groups.ClientGroup;
import com.chris.dto.groups.MerchantGroup;
import com.chris.dto.groups.RiderGroup;
import com.chris.enumeration.RoleType;
import com.chris.service.RegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import com.chris.vo.resultVOs.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

import static com.chris.constant.MessageConstant.UNEXPECTED_USER_ROLE;

@RestController
@RequestMapping("/register")
@Validated
@Tag(name = "RegistrationController", description = "Register-related APIs")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private Validator validator;

    @PostMapping
    @Operation(summary = "User registration", description = "Register new account for Client/Merchant/Rider")
    public Result<String> register(@RequestBody RegistrationDTO dto) {
        RoleType role = dto.getUser().getRole();

        if (role == null) {
            return Result.error(UNEXPECTED_USER_ROLE);
        }

        switch (role) {
            case MERCHANT -> validate(dto, MerchantGroup.class);
            case CLIENT   -> validate(dto, ClientGroup.class);
            case RIDER    -> validate(dto, RiderGroup.class);
            default       -> throw new IllegalArgumentException(UNEXPECTED_USER_ROLE+ ": " + role);
        }

        registrationService.register(dto);
        return Result.success("Registration successful");
    }

    private void validate(RegistrationDTO dto, Class<?> group) {
        Set<ConstraintViolation<RegistrationDTO>> violations = validator.validate(dto, group);

        if (!violations.isEmpty()) {
            // 将所有字段的错误信息格式化为 "字段: 错误" 形式
            String message = violations.stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .collect(Collectors.joining("; "));

            throw new IllegalArgumentException("Validation failed: " + message);
        }
    }
}

