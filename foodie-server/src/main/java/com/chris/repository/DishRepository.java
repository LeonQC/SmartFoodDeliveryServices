package com.chris.repository;

import com.chris.entity.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DishRepository extends JpaRepository<Dish, Long>, JpaSpecificationExecutor<Dish> {
    long countByCategoryMerchantUserUserId(Long userId);

    long countByCategoryMerchantUserUserIdAndStatus(Long userId, short i);
}
