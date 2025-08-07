package com.chris.repository;

import com.chris.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    @EntityGraph(attributePaths = {"client", "merchant", "rider"})
    Optional<User> findDetailedByUserId(Long id);

    Optional<User> findByUserId(Long userId);

    /**
     * 统计每一天的“新用户”数量：即在这一天下了第一单的用户数
     * 思路：先找出每个用户在这个商户下的第一次下单日期，再按天去统计这些第一次下单的人数
     */
    @Query(value = """
    SELECT
      to_char(t.first_date, 'YYYY-MM-DD') AS date,
      COUNT(*)                            AS newClients
    FROM (
      SELECT
        c.id,
        MIN(o.paid_at)::date AS first_date
      FROM clients c
      JOIN orders o    ON c.id = o.client_id
      JOIN merchants m ON o.merchant_id = m.id
      WHERE m.user_id   = :userId
        AND o.paid_at  >= :startDate
      GROUP BY c.id
    ) t
    WHERE t.first_date >= :startDate
    GROUP BY t.first_date
    ORDER BY t.first_date
    """, nativeQuery = true)
    List<Object[]> findNewUserTrendByUser(@Param("userId") Long userId, @Param("startDate") LocalDate startDate);

}

