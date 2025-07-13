package com.chris.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BrowseDTO {
    @NotNull
    private Double latitude;
    @NotNull
    private Double longitude;

    private String merchantName;

    private Short merchantStatus;

    private Integer pageSize=5;

    /** 游标字段，初始可以置为 -1 */
    private Double lastValue = null;
    private Long   lastId    = null;
    private String sortField = "distance"; // 排序字段：distance, likeCount, favoriteCount, commentCount, rating
}
