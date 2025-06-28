package com.chris.entity;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.TypeDef;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Point;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Entity
@Table(name = "merchants")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Merchant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long merchantId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private String phone;
    @Column(nullable = false)
    private String address;
    @Column(nullable = false)
    private String city;
    @Column(nullable = false)
    private String state;

    private String zipcode;

    @Column(nullable = false)
    private String country;

    @Column(name = "x", nullable = false)
    private Double longitude;

    @Column(name = "y", nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private String merchantName;

    @Column(name= "merchant_description")
    private String merchantDescription;
    @Column(name= "merchant_image")
    private String merchantImage;
    @Column(name= "merchant_type")
    private String merchantType;
    @Column(name= "merchant_social_media")
    private String merchantSocialMedia;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(
            name = "merchant_opening_hours",
            columnDefinition = "jsonb",  // 确保 DDL 中使用 jsonb 类型
            nullable = false
    )
    private Map<String, String> merchantOpeningHours;

    private Short merchantStatus;

    @JdbcTypeCode(SqlTypes.GEOMETRY)
    @Column(columnDefinition = "geometry(Point, 4326)", nullable = false)
    private Point location;

    @OneToMany(mappedBy = "merchant", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Category> category  = new ArrayList<>();;

    @OneToMany(mappedBy = "merchant", fetch = FetchType.LAZY)
    private List<Order> orders;
}
