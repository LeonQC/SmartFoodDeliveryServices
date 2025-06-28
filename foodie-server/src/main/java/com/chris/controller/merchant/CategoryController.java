package com.chris.controller.merchant;

import com.chris.context.UserContext;
import com.chris.dto.CategoryDTO;
import com.chris.service.CategoryService;
import com.chris.vo.CategoryVO;
import com.chris.vo.resultVOs.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/merchant/categories")
@Tag(name = "CategoryController", description = "Category-related APIs")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping
    @Operation(summary = "Query all categories", description = "Query all categories of current merchant")
    public Result<List<CategoryVO>> getCategories() {
        Long userId = UserContext.getCurrentId();
        return categoryService.getCategories(userId);
    }

    @DeleteMapping
    @Operation(summary = "Delete categories", description = "Delete categories for the current merchant, either in batch or by specific category ID")
    public Result<String> deleteCategories(@RequestParam Long[] categoryIds) {
        categoryService.deleteCategories(categoryIds);
        return Result.success("Category deleted successfully");
    }

    @PostMapping
    @Operation(summary = "Create category", description = "Create a new category for the current merchant")
    public Result<String> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        categoryService.createCategory(categoryDTO);
        return Result.success("Category created successfully");
    }

    @GetMapping("/{categoryId}")
    @Operation(summary = "Query category", description = "Query a specific category by ID")
    public Result<CategoryVO> getCategory(@PathVariable Long categoryId) {
        return categoryService.getCategory(categoryId);
    }

    @PutMapping("/{categoryId}")
    @Operation(summary = "Update category", description = "Update a specific category by ID")
    public Result<String> updateCategory(@PathVariable Long categoryId, @Valid @RequestBody CategoryDTO categoryDTO) {
        categoryService.updateCategory(categoryId, categoryDTO);
        return Result.success("Category updated successfully");
    }

    @PutMapping("/{categoryId}/status/{status}")
    @Operation(summary = "Update category status", description = "Update the status of a specific category by ID")
    public Result<String> changeCategoryStatus(@PathVariable Long categoryId, @PathVariable Short status) {
        categoryService.updateCategoryStatus(categoryId, status);
        String msg = status == 1 ? "Category enabled successfully" : "Category disabled successfully";
        return Result.success(msg);
    }
}
