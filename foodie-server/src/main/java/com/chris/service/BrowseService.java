package com.chris.service;

import com.chris.dto.BrowseDTO;
import com.chris.vo.AllRestaurantsVO;
import com.chris.vo.DishVO;
import com.chris.vo.RestaurantVO;
import com.chris.vo.resultVOs.Result;
import com.chris.vo.resultVOs.ScrollResult;

import java.util.List;

public interface BrowseService {

    Result<ScrollResult<AllRestaurantsVO, ?>> browse(BrowseDTO browseDTO);

    Result<RestaurantVO> browseMerchant(Long merchantId);

    Result<List<DishVO>> browseDishes(Long merchantId, Long categoryId);
}
