package com.chris.service;

import com.chris.dto.DishPayloadDTO;
import com.chris.dto.DishQueryDTO;
import com.chris.vo.dashboardVOs.DashboardCategoryDishStatusVO;
import com.chris.vo.DishVO;
import com.chris.vo.resultVOs.PageResult;

public interface DishService {
    PageResult<DishVO> getDishes(DishQueryDTO dishQueryDTO);

    void deleteDishes(Long[] dishIds);

    void createDish(DishPayloadDTO dto);

    DishVO getDish(Long dishId);

    void updateDish(Long dishId, DishPayloadDTO dto);

    void updateDishStatus(Long dishId, Short status);

    DashboardCategoryDishStatusVO getCategoryDishStatus(Long userId);
}
