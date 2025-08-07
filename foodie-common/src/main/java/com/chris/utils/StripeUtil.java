package com.chris.utils;

import com.chris.properties.StripeProperties;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import org.springframework.stereotype.Component;

/**
 * Stripe 支付工具类
 */
@Component
public class StripeUtil {
    private final String webhookSecret;

    // 推荐直接注入 StripeProperties 以便后续扩展更多参数
    public StripeUtil(StripeProperties stripeProperties) {
        this.webhookSecret = stripeProperties.getWebhookSecret();
    }

    /**
     * 创建 PaymentIntent
     */
    public String createPaymentIntent(Long orderId, Long userId, long amount, String currency) {
        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amount)
                    .setCurrency(currency)
                    //.addPaymentMethodType("paypal")   // 测试账号或未专业申请的正是账号无法使用 PayPal
                    .addPaymentMethodType("card")     // 支持 Apple Pay, Google Pay, Credit/Debit Cards
                    .putMetadata("orderId", String.valueOf(orderId)) // 响应回调时，webhook会带上orderId，用于处理接下来的后端逻辑
                    .putMetadata("userId", String.valueOf(userId))
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);
            return intent.getClientSecret();
        } catch (StripeException e) {
            throw new RuntimeException("Stripe创建支付单失败", e);
        }
    }
    /**
     * 校验 Stripe webhook 签名并解析事件
     *
     * @param payload        请求体
     * @param sigHeader      Stripe-Signature 请求头
     * @return Event
     * @throws SignatureVerificationException 如果签名错误
     */
    public Event parseWebhookEvent(String payload, String sigHeader)
            throws SignatureVerificationException {
        return Webhook.constructEvent(payload, sigHeader, webhookSecret);
    }

    /**
     * 发起 Stripe 退款
     */
    public void refundPaymentIntent(String paymentIntentId, Long amount) throws StripeException {
        RefundCreateParams.Builder builder = RefundCreateParams.builder()
                .setPaymentIntent(paymentIntentId);
        if (amount != null && amount > 0) {
            builder.setAmount(amount);
        }
        RefundCreateParams params = builder.build();
        Refund.create(params);
    }
}
