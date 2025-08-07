package com.chris.repository;

import com.chris.vo.AllRestaurantsVO;

import java.util.List;

public interface MerchantRepositoryCustom {
    List<AllRestaurantsVO> fetchByDistance(
            double lng,
            double lat,
            String merchantName,
            Short merchantStatus,
            Double lastDistance,
            Long lastId,
            int pageSize
    );
}
