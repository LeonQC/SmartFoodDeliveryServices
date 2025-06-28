package com.chris.controller.account;

import com.chris.context.UserContext;
import com.chris.dto.ProfileUpdateDTO;
import com.chris.service.ProfileService;
import com.chris.vo.profileVOs.ProfileVO;
import com.chris.vo.resultVOs.Result;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @GetMapping
    @Operation(summary = "View profile", description = "View the profile of current user")
    public Result<ProfileVO> viewProfile() {
        Long userId = UserContext.getCurrentId();
        ProfileVO profile = profileService.getProfile(userId);
        return Result.success(profile);
    }

    @PutMapping
    @Operation(summary = "Update profile", description = "Update the profile of current user")
    public Result<ProfileVO> updateProfile(@RequestBody @Valid ProfileUpdateDTO dto) {
        Long userId = UserContext.getCurrentId();
        ProfileVO profile = profileService.updateProfile(userId, dto);
        return Result.success("Profile updated successfully");
    }
}
