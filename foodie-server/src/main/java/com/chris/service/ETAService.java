package com.chris.service;

public interface ETAService {
    long estimateDeliveryTime(Long merchantId, Long addressId, Long userId);
}
