package com.chris.repository;

import com.chris.dto.UserOrderHistoryDTO;
import com.chris.entity.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    List<Order> findByMerchantUserUserIdAndCreateTimeBetween(Long userId, LocalDateTime start, LocalDateTime end);

    List<Order> findByMerchantUserUserIdAndCreateTimeBefore(Long userId, LocalDateTime start);

    @Query("""
      select distinct o
      from Order o
      left join fetch o.riderAssignments ra
      where o.merchant.user.userId = :userId
        and o.status = :status
      """)
    List<Order> findByMerchantUserIdAndStatus(
            @Param("userId") Long userId,
            @Param("status") Short status
    );

    @EntityGraph(
            attributePaths = {
                    // FetchMode.SUBSELECT   把 OrderItem 和每个 item 关联的 Dish 一次性加载
                    "address",               // 加载地址
                    // FetchMode.SUBSELECT   加载状态变更日志
                    "riderAssignments"       // （如果你也需要在详情里用）加载骑手派单记录
            }
    )
    Optional<Order> findByOrderIdAndMerchantUserUserId(Long orderId, Long userId);


    @Query(value = """
    SELECT to_char(o.create_time, 'YYYY-MM-DD') AS date,
           SUM(o.total_amount)               AS value
    FROM orders o
    JOIN merchants m ON o.merchant_id = m.id
    WHERE m.user_id = :userId
      AND o.create_time >= :startDate
    GROUP BY date
    ORDER BY date
    """, nativeQuery = true)
    List<Object[]> findRevenueTrendByUser(@Param("userId") Long userId,@Param("startDate") LocalDate start);


    @Query(value = """
    SELECT to_char(o.create_time, 'YYYY-MM-DD') AS date,
           COUNT(*)                   AS total,
           SUM(CASE WHEN o.status <> 7 THEN 1 ELSE 0 END) AS valid
    FROM orders o
    JOIN merchants m ON o.merchant_id = m.id
    WHERE m.user_id = :userId
      AND o.create_time >= :startDate
    GROUP BY date
    ORDER BY date
    """, nativeQuery = true)
    List<Object[]> findOrderTrendByUser(@Param("userId") Long userId,@Param("startDate") LocalDate start);


    List<Order> findByRiderAssignmentsRiderUserUserIdAndStatusIn(Long userId, List<Short> statusList);

    @Query("""
      SELECT new com.chris.dto.UserOrderHistoryDTO(oi.dishName, SUM(oi.quantity))
      FROM Order o
      JOIN o.items oi
      WHERE o.client.user.userId = :userId
          AND o.createTime >= :startTime
      GROUP BY oi.dishName
      ORDER BY SUM(oi.quantity) DESC
    """)
    List<UserOrderHistoryDTO> findUserOrderHistory(@Param("userId") Long userId, @Param("startTime") LocalDateTime startTime);
}
