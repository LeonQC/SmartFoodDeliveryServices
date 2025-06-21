package com.chris.service.impl;

import com.chris.dto.RegistrationDTO;
import com.chris.entity.User;
import com.chris.exception.UsernameAlreadyExistsException;
import com.chris.mapper.RegistrationMapper;
import com.chris.repository.UserRepository;
import com.chris.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.chris.constant.MessageConstant.USERNAME_ALREADY_EXISTS;

@Service
public class RegistrationServiceImpl implements RegistrationService {
    @Autowired
    private RegistrationMapper mapper;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void register(RegistrationDTO dto) {
        String username = dto.getUser().getUsername();
        if (userRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException(USERNAME_ALREADY_EXISTS);
        }
        User user = mapper.toUser(dto);
        userRepository.save(user);
    }
}
