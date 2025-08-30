package com.chris.dto;

import lombok.Data;

@Data
public class OrderCompletedEvent {
    private Long orderId;
    private Long clientUserId;
    private Long merchantUserId;
    private Long riderUserId;

    // 全链路节点时间戳（毫秒/秒，建议用毫秒时间戳）
    private Long createTime;            //status: null -> 0
    private Long payTime;               //status: 0 -> 1
    private Long merchantAcceptTime;    //status: 1 -> 2
    private Long prepareTime;           //status: 2 -> 3
    private Long riderAcceptTime;       //status: 3 -> 4
    private Long pickUpTime;            //status: 4 -> 5 Map API
    private Long completeTime;          //status: 5 -> 6 Map API

    // 各阶段耗时（单位：毫秒，可按需换成秒）
    private Long payDuration;               // 创建->支付
    private Long merchantAcceptDuration;    // 支付->商家接单
    private Long prepareDuration;           // 商家接单->备餐完成
    private Long waitRiderDuration;         // 备餐完成->骑手接单
    private Long pickUpDuration;            // 骑手接单->取餐
    private Long deliveryDuration;          // 取餐->送达
    private Long totalDuration;             // 创建->完成
}