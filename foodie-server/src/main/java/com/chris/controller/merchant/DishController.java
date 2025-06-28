package com.chris.controller.merchant;

import com.chris.dto.DishPayloadDTO;
import com.chris.dto.DishQueryDTO;
import com.chris.service.DishService;
import com.chris.vo.DishVO;
import com.chris.vo.resultVOs.PageResult;
import com.chris.vo.resultVOs.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/merchant/dishes")
@Tag(name = "DishController", description = "Dish-related APIs")
public class DishController {
    @Autowired
    private DishService dishService;

    @PostMapping("/search")
    @Operation(summary = "Query dishes", description = "Query dishes of the current merchant with page and other conditions")
    public Result<PageResult<DishVO>> getDishes(@RequestBody DishQueryDTO dishQueryDTO) {
        PageResult<DishVO> pageResult = dishService.getDishes(dishQueryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping
    @Operation(summary = "Delete dishes", description = "Delete dishes for the current merchant, either in batch or by specific dish ID")
    public Result<String> deleteDishes(@RequestParam Long[] dishIds) {
        dishService.deleteDishes(dishIds);
        return Result.success("Dish deleted successfully");
    }

    @PostMapping
    @Operation(summary = "Create dish", description = "Create a new dish for the current merchant")
    public Result<String> createDish(@Valid @RequestBody DishPayloadDTO dto) {
        dishService.createDish(dto);
        return Result.success("Dish created successfully");
    }

    @GetMapping("/{dishId}")
    @Operation(summary = "Get dish", description = "Get a specific dish for the current merchant")
    public Result<DishVO> getDish(@PathVariable Long dishId) {
        DishVO dishVO = dishService.getDish(dishId);
        return Result.success(dishVO);
    }

    @PutMapping("/{dishId}")
    @Operation(summary = "Update dish", description = "Update a specific dish for the current merchant")
    public Result<String> updateDish(@PathVariable Long dishId, @Valid @RequestBody DishPayloadDTO dto) {
        dishService.updateDish(dishId, dto);
        return Result.success("Dish updated successfully");
    }

    @PutMapping("/{dishId}/status/{status}")
    @Operation(summary = "Update dish status", description = "Update the status of a specific dish for the current merchant")
    public Result<String> updateDishStatus(@PathVariable Long dishId, @PathVariable Short status) {
        dishService.updateDishStatus(dishId, status);
        String msg = status == 1 ? "Dish enabled successfully" : "Dish disabled successfully";
        return Result.success(msg);
    }
}
