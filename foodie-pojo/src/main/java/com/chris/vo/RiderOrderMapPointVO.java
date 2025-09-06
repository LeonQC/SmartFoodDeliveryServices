package com.chris.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RiderOrderMapPointVO {
    private Long orderId;
    private String merchantName;
    private double merchantLng;
    private double merchantLat;
    private String destinationName;
    private double destinationLng;
    private double destinationLat;
}
