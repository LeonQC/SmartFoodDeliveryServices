package com.chris.service;

import com.chris.dto.RiderLocationDTO;
import com.chris.vo.resultVOs.Result;

public interface RiderService {
    Result<Short> getStatus(Long userId);

    void changeStatus(Long userId, Short newStatus);

    void updateLocation(Long userId, RiderLocationDTO dto);
}
