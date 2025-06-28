package com.chris.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 骑手分配记录：记录每次尝试将订单分配给骑手的结果
 */
@Data
@Entity
@Table(name = "rider_assignments")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RiderAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long riderAssignmentId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rider_id", nullable = false)
    private Rider rider;

    @Column(name = "status", nullable = false)
    private Short status;

    @CreationTimestamp
    @Column(name = "attempt_at", nullable = false, updatable = false)
    private LocalDateTime attemptAt;
}
