package com.chris.service;

import com.chris.dto.ProfileUpdateDTO;
import com.chris.vo.ProfileVO;

public interface ProfileService {
    ProfileVO getProfile(Long userId);

    ProfileVO updateProfile(Long userId, ProfileUpdateDTO dto);
}
