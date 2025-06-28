package com.chris.repository;

import com.chris.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query(value = """
    SELECT d.name             AS name,
           SUM(oi.quantity)   AS sales
    FROM order_items oi
    JOIN orders o    ON oi.order_id = o.id
    JOIN merchants m ON o.merchant_id = m.id
    JOIN dishes d    ON oi.dish_id = d.id
    WHERE m.user_id   = :userId
      AND o.create_time  >= :startDate
    GROUP BY d.name
    ORDER BY sales DESC
    LIMIT 10
    """, nativeQuery = true)
    List<Object[]> findTopDishesByUser(@Param("userId") Long userId, @Param("startDate") LocalDate startDate);
}
