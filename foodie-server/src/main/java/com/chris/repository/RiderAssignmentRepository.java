package com.chris.repository;

import com.chris.entity.RiderAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RiderAssignmentRepository extends JpaRepository<RiderAssignment, Long> {

    List<RiderAssignment> findByRiderUserUserIdAndAttemptAtBetween(Long userId, LocalDateTime start, LocalDateTime end);

    boolean existsByOrderOrderIdAndStatus(Long orderId, short pickingUp);

    Optional<RiderAssignment> findByOrderOrderIdAndRiderUserUserId(Long orderId, Long userId);

    boolean existsByOrderOrderIdAndRiderUserUserId(Long orderId, Long riderUserId);

    Collection<Long> findRiderUserUserIdsByOrderOrderId(Long orderId);

    List<RiderAssignment> findAllByOrderOrderId(Long orderId);

    List<RiderAssignment> findAllByRiderUserUserId(Long userId);
}