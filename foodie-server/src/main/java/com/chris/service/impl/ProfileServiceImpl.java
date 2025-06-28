package com.chris.service.impl;

import com.chris.dto.ProfileUpdateDTO;
import com.chris.entity.User;
import com.chris.exception.UserNotFoundException;
import com.chris.mapper.ProfileMapper;
import com.chris.repository.UserRepository;
import com.chris.service.ProfileService;
import com.chris.vo.ProfileVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.chris.constant.MessageConstant.USER_NOT_FOUND;

@Service
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public ProfileVO getProfile(Long userId) {
        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
        return mapper.toProfileVO(user);
    }

    @Override
    @Transactional
    public ProfileVO updateProfile(Long userId, ProfileUpdateDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));

        // 1. 更新 User 本身（包括把 profileCompleted 设为 true）
        mapper.updateUserFromDto(dto, user);
        // 2. 根据角色更新子实体
        switch (user.getRole()) {
            case MERCHANT -> mapper.updateMerchantFromDto(dto, user.getMerchant());
            case CLIENT   -> mapper.updateClientFromDto(dto, user.getClient());
            case RIDER    -> mapper.updateRiderFromDto(dto, user.getRider());
        }
        // 3. 持久化并返回最新的 VO
        user = userRepository.save(user);
        return mapper.toProfileVO(user);
    }
}
