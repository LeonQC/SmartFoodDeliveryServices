package com.chris.service.impl;

import com.chris.dto.UserOrderHistoryDTO;
import com.chris.dto.AllDishDTO;
import com.chris.repository.DishRepository;
import com.chris.repository.OrderRepository;
import com.chris.service.RecommendService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.chris.vo.RecommendItemVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.client.OpenAIClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class RecommendServiceImpl implements RecommendService {

    @Autowired
    private OpenAIClient openAIClient;
    @Autowired
    private DishRepository dishRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public List<RecommendItemVO> recommendDishesForUser(Long userId) {
        // 1. 查出用户历史订单和所有菜品
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        List<UserOrderHistoryDTO> userOrders = orderRepository.findUserOrderHistory(userId, oneMonthAgo); // [{name,count}]
        List<AllDishDTO> allDishes = dishRepository.findAllDishesForRecommend(); // [{dishId,name,imageUrl,merchantId}]

        // 2. 拼接 prompt
        String prompt = String.format("""
                        你是推荐系统。只返回 JSON 数组，不要任何额外文字、解释或代码块。
                        目标格式：[{ "dishId": number, "dishName": string, "imageUrl": string, "merchantId": number }]
                        若无法给出5个，返回尽可能多的有效项。
                        
                        【用户历史订单】
                        %s
                        
                        【可选菜品】（请优先与历史偏好相近的）
                        %s
                        """,
                toJson(userOrders), toJson(allDishes)
        );

        // 3. 构造 OpenAI 请求参数
        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .addUserMessage(prompt)
                .model(ChatModel.GPT_4O_MINI)
                .temperature(0.2)
                .maxTokens(400)
                .build();

        String aiContent;
        try {
            ChatCompletion response = openAIClient.chat().completions().create(params);
            var message = response.choices().get(0).message();
            aiContent = String.valueOf(message.content());
            // 兜底：去掉可能的代码块包装，并做空值检查
            if (aiContent == null) {
                log.warn("AI返回为空：{}", message);
                return getDefaultRecommend();
            }
            aiContent = aiContent.trim();
            if (aiContent.startsWith("```")) {
                aiContent = aiContent.replaceAll("^```(?:json)?\\s*|\\s*```$", "");
            }

            log.info("[AI返回] 推荐结果: {}", aiContent);
        } catch (Exception e) {
            log.error("OpenAI推荐失败", e);
            return getDefaultRecommend();
        }

        // 4. 反序列化
        try {
            List<RecommendItemVO> list = objectMapper.readValue(aiContent, new TypeReference<List<RecommendItemVO>>() {});
            for (RecommendItemVO vo : list) {
                vo.setMerchantUrl("/client/browse/" + vo.getMerchantId());
            }
            return list;
        } catch (Exception e) {
            log.error("AI结果解析失败: {}", aiContent, e);
            return getDefaultRecommend();
        }
    }

    private String toJson(Object o) {
        try { return objectMapper.writeValueAsString(o); }
        catch (Exception e) { return "[]"; }
    }
    private List<RecommendItemVO> getDefaultRecommend() {
        // 可以查数据库返回一些热门菜品兜底
        return List.of();
    }
}
