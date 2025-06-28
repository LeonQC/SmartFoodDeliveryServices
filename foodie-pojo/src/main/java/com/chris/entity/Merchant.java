package com.chris.entity;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.TypeDef;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Point;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Map;

@Data
@Entity
@Table(name = "merchants")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Merchant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long merchantId;

    @OneToOne(cascade = CascadeType.ALL)
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

    private String merchantDescription;
    private String merchantImage;
    private String merchantType;
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

}
