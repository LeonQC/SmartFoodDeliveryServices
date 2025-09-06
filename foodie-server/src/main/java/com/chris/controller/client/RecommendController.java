package com.chris.controller.client;

import com.chris.context.UserContext;
import com.chris.service.RecommendService;
import com.chris.vo.RecommendItemVO;
import com.chris.vo.resultVOs.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/client/recommend")
public class RecommendController {

    @Autowired
    private RecommendService recommendService;

    @GetMapping("/dishes")
    public Result<List<RecommendItemVO>> recommendDishes() {
        // 获取登录用户ID（UserContext.getCurrentId()，或用token解析）
        Long userId = UserContext.getCurrentId();
        List<RecommendItemVO> list = recommendService.recommendDishesForUser(userId);
        return Result.success(list);
    }
}
