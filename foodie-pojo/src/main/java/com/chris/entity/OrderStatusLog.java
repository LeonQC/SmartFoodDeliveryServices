package com.chris.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 订单状态变更日志：记录订单状态每次变化的前后状态、操作者和时间
 */
@Data
@Entity
@Table(name = "order_status_log")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class OrderStatusLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long orderStatusLogId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "from_status", nullable = false)
    private Short fromStatus;

    @Column(name = "to_status", nullable = false)
    private Short toStatus;

    @Column(name = "changed_by", length = 50, nullable = false)
    private String changedBy;

    @Column
    private String remark;

    @CreationTimestamp
    @Column(name = "changed_at", nullable = false, updatable = false)
    private LocalDateTime changedAt;
}
