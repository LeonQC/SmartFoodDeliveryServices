package com.chris.repository;

import com.chris.entity.ShoppingCart;
import org.locationtech.jts.geom.CoordinateSequenceFilter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    // 取单条：根据 userId 和 dishId
    Optional<ShoppingCart> findByClient_User_UserIdAndDish_DishId(Long userId, Long dishId);

    // 列表：根据 userId
    List<ShoppingCart> findByClient_User_UserId(Long userId);

    // 清空：根据 userId
    void deleteByClient_User_UserId(Long userId);

    List<ShoppingCart> findAllByClient_User_UserIdAndCartIdIn(Long userId, List<Long> cartIdList);
}
