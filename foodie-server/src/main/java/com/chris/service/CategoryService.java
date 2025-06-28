package com.chris.service;

import com.chris.dto.CategoryDTO;
import com.chris.vo.CategoryVO;
import com.chris.vo.resultVOs.Result;

import java.util.List;

public interface CategoryService {
    Result<List<CategoryVO>> getCategories(Long userId);

    void deleteCategories(Long[] categoryIds);

    void createCategory(CategoryDTO categoryDTO);

    Result<CategoryVO> getCategory(Long categoryId);

    void updateCategory(Long categoryId, CategoryDTO categoryDTO);

    void updateCategoryStatus(Long categoryId, Short status);
}
