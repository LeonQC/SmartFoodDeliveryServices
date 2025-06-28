package com.chris.service.impl;

import com.chris.entity.Merchant;
import com.chris.exception.MerchantNotFoundException;
import com.chris.repository.UserRepository;
import com.chris.service.MerchantService;
import com.chris.vo.resultVOs.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.chris.constant.MessageConstant.USER_IS_NOT_MERCHANT;

@Service
public class MerchantServiceImpl implements MerchantService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public Result<Short> getStatus(Long userId) {
        Merchant merchant = userRepository.findByUserId(userId)
                .orElseThrow(() -> new MerchantNotFoundException(USER_IS_NOT_MERCHANT))
                .getMerchant();
        return Result.success(merchant.getMerchantStatus());
    }

    @Override
    @Transactional
    public void changeStatus(Long userId, Short newStatus) {
        Merchant merchant = userRepository.findByUserId(userId)
                .orElseThrow(() -> new MerchantNotFoundException(USER_IS_NOT_MERCHANT))
                .getMerchant();
        merchant.setMerchantStatus(newStatus);
    }
}
