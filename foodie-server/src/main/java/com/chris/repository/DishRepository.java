package com.chris.repository;

import com.chris.entity.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface DishRepository extends JpaRepository<Dish, Long>, JpaSpecificationExecutor<Dish> {
    long countByCategoryMerchantUserUserId(Long userId);

    long countByCategoryMerchantUserUserIdAndStatus(Long userId, short i);

    List<Dish> findByCategory_Merchant_MerchantIdAndCategory_CategoryId(Long merchantId, Long categoryId);
}
