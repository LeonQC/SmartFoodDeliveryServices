package com.chris.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DeliveryFeeUtil {
    private static final BigDecimal BASE_FEE = BigDecimal.valueOf(3.0);
    private static final int BASE_DISTANCE = 3000; // 米
    private static final BigDecimal FEE_PER_KM = BigDecimal.valueOf(1.0);
    private static final BigDecimal MAX_FEE = BigDecimal.valueOf(20.0);

    public static BigDecimal calcFee(double distance) {
        if (distance <= BASE_DISTANCE) return BASE_FEE;
        int extraKm = (int) Math.ceil((distance - BASE_DISTANCE) / 1000.0);
        BigDecimal fee = BASE_FEE.add(FEE_PER_KM.multiply(BigDecimal.valueOf(extraKm)));
        return fee.min(MAX_FEE).setScale(2, RoundingMode.HALF_UP);
    }
}