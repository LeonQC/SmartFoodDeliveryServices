package com.chris.service;

import com.chris.vo.DeliveryFeeVO;
import com.chris.vo.resultVOs.Result;

public interface DeliveryFeeService {
    Result<DeliveryFeeVO> calcFee(Long merchantId, Long addressId);
}
