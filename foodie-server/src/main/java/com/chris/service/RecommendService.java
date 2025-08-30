package com.chris.service;

import com.chris.vo.RecommendItemVO;

import java.util.List;

public interface RecommendService {
    List<RecommendItemVO> recommendDishesForUser(Long userId);
}