package com.chris.service;

import com.chris.vo.resultVOs.Result;

public interface MerchantService {
    Result<Short> getStatus(Long userId);

    void changeStatus(Long userId, Short newStatus);
}
