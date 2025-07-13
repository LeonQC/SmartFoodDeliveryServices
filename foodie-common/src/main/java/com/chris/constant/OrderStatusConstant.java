package com.chris.constant;

public class OrderStatusConstant {
    private OrderStatusConstant() {}  // 防止实例化

    public static final short PENDING         = 0;   // 待支付
    public static final short PAID            = 1;   // 已支付（待接单）
    public static final short ACCEPTED        = 2;   // 已接单（备餐中）
    public static final short READY           = 3;   // 已就绪（待分派骑手）
    public static final short PICKING_UP      = 4;   // 待取餐
    public static final short DISPATCHING     = 5;   // 配送中
    public static final short COMPLETED       = 6;   // 完成
    public static final short CANCELLED       = 7;   // 取消
}
