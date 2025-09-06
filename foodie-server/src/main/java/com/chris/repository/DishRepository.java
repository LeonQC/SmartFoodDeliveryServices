package com.chris.repository;

import com.chris.dto.AllDishDTO;
import com.chris.entity.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DishRepository extends JpaRepository<Dish, Long>, JpaSpecificationExecutor<Dish> {
    long countByCategoryMerchantUserUserId(Long userId);

    long countByCategoryMerchantUserUserIdAndStatus(Long userId, short i);

    List<Dish> findByCategory_Merchant_MerchantIdAndCategory_CategoryId(Long merchantId, Long categoryId);


    @Query("""
      SELECT new com.chris.dto.AllDishDTO(d.dishId, d.name, d.image, d.category.merchant.merchantId)
      FROM Dish d
      WHERE d.status = 1
    """)
    List<AllDishDTO> findAllDishesForRecommend();
}
