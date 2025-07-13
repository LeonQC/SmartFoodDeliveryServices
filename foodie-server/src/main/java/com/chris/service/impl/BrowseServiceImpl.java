package com.chris.service.impl;

import com.chris.dto.BrowseDTO;
import com.chris.entity.Dish;
import com.chris.entity.Merchant;
import com.chris.exception.MerchantNotFoundException;
import com.chris.mapper.DishMapper;
import com.chris.mapper.RestaurantMapper;
import com.chris.repository.DishRepository;
import com.chris.repository.RestaurantRepository;
import com.chris.service.BrowseService;
import com.chris.utils.GeoUtil;
import com.chris.vo.AllRestaurantsVO;
import com.chris.vo.DishVO;
import com.chris.vo.RestaurantVO;
import com.chris.vo.resultVOs.Result;
import com.chris.vo.resultVOs.ScrollResult;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.chris.constant.MessageConstant.MERCHANT_NOT_FOUND;

@Service
public class BrowseServiceImpl implements BrowseService {
    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    private RestaurantMapper restaurantMapper;
    @Autowired
    private DishRepository dishRepository;
    @Autowired
    private DishMapper dishMapper;

    @Override
    public Result<ScrollResult<AllRestaurantsVO, ?>> browse(BrowseDTO dto) {
        double lng = dto.getLongitude();
        double lat = dto.getLatitude();
        List<AllRestaurantsVO> rows = new ArrayList<>();
        // —— 1) distance 排序特例 ——
        if ("distance".equals(dto.getSortField())) {
            rows = restaurantRepository.fetchByDistance(
                    lng, lat,
                    dto.getMerchantName(),
                    dto.getMerchantStatus(),
                    dto.getLastValue(),
                    dto.getLastId(),
                    dto.getPageSize()
            );
        }
        // V = Double
        ScrollResult<AllRestaurantsVO, Double> page = ScrollResult
                .of(rows, AllRestaurantsVO::getDistance, AllRestaurantsVO::getMerchantId);
        return Result.success(page);


    }

    @Override
    public Result<RestaurantVO> browseMerchant(Long merchantId) {
        Merchant m = restaurantRepository.findByMerchantId(merchantId);
        if (m == null) {
            throw new MerchantNotFoundException(MERCHANT_NOT_FOUND);
        }
        RestaurantVO restaurantVO = restaurantMapper.toRestaurantVO(m);
        return Result.success(restaurantVO);
    }

    @Override
    public Result<List<DishVO>> browseDishes(Long merchantId, Long categoryId) {
        List<Dish> dishes = dishRepository
                .findByCategory_Merchant_MerchantIdAndCategory_CategoryId(merchantId, categoryId);

        // 用不带 category 的映射
        List<DishVO> vos = dishMapper.toVOListWithoutCategory(dishes);
        return Result.success(vos);
    }
}
