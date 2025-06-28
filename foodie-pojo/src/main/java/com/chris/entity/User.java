package com.chris.entity;

import com.chris.enumeration.RoleType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long userId;

    @Column(nullable = false, unique = true)
    private String username;

    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleType role;

    @Column(nullable = false)
    private Boolean profileCompleted;

    // 添加创建和更新时间
    @Column(name = "create_time", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updateTime;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Client client;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Merchant merchant;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Rider rider;
}
