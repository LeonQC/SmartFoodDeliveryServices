package com.chris.controller.client;

import com.chris.dto.BrowseDTO;
import com.chris.service.BrowseService;
import com.chris.vo.AllRestaurantsVO;
import com.chris.vo.DishVO;
import com.chris.vo.RestaurantVO;
import com.chris.vo.resultVOs.Result;
import com.chris.vo.resultVOs.ScrollResult;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/client/browse")
public class BrowseController {

    @Autowired
    private BrowseService browseService;

    @PostMapping
    @Operation(summary = "Browse all restaurants", description = "Browse all restaurants by special criteria")
    public Result<ScrollResult<AllRestaurantsVO, ?>> browse(@RequestBody BrowseDTO browseDTO) {
        return browseService.browse(browseDTO);
    }

    @GetMapping("/merchants/{merchantId}")
    @Operation(summary = "Browse the restaurant", description = "Browse a restaurant and belonged categories by merchantId")
    public Result<RestaurantVO> browseMerchant(@PathVariable Long merchantId) {
        return browseService.browseMerchant(merchantId);
    }

    @GetMapping("/merchants/{merchantId}/categories/{categoryId}")
    @Operation(summary = "Browse the dishes", description = "Browse dishes by categoryId and merchantId")
    public Result<List<DishVO>> browseDishes(@PathVariable Long merchantId, @PathVariable Long categoryId) {
        return browseService.browseDishes(merchantId, categoryId);
    }
}
