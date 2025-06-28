package com.chris.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long categoryId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private Integer sort;

    @Column(nullable = false)
    private Short status;

    @Column(name= "create_time", nullable = false)
    @CreationTimestamp
    private LocalDateTime createTime;

    @Column(name= "update_time",nullable = false)
    @UpdateTimestamp
    private LocalDateTime updateTime;

    @OneToMany(mappedBy = "category")
    private List<Dish> dishes = new ArrayList<>();
}
