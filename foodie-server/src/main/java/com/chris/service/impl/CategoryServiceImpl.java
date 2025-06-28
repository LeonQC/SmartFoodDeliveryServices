package com.chris.service.impl;

import com.chris.context.UserContext;
import com.chris.dto.CategoryDTO;
import com.chris.entity.Category;
import com.chris.entity.Merchant;
import com.chris.exception.CategoryAlreadyExistException;
import com.chris.exception.CategoryDeleteFailedException;
import com.chris.exception.CategoryNotFoundException;
import com.chris.exception.UserNotFoundException;
import com.chris.mapper.CategoryMapper;
import com.chris.repository.CategoryRepository;
import com.chris.repository.UserRepository;
import com.chris.service.CategoryService;
import com.chris.vo.CategoryVO;
import com.chris.vo.resultVOs.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.chris.constant.MessageConstant.*;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryMapper mapper;

    @Override
    public Result<List<CategoryVO>> getCategories(Long userId) {
        List<Category> categories = categoryRepository.findAllByMerchant_User_UserId(userId);
        List<CategoryVO> categoryVOs = categories.stream()
                .map(mapper::toVO)
                .toList();
        return Result.success(categoryVOs);
    }

    @Override
    @Transactional
    public void deleteCategories(Long[] categoryIds) {
        for (Long id: categoryIds) {
            Category cat = categoryRepository.findById(id)
                    .orElseThrow(() -> new CategoryNotFoundException(CATEGORY_NOT_FOUND));
            try {
                categoryRepository.delete(cat);
                categoryRepository.flush();
            } catch (DataIntegrityViolationException ex) {
                throw new CategoryDeleteFailedException(String.format("分类 '%s' 下还有菜品，无法删除", cat.getName()));
            }
        }
    }

    @Override
    @Transactional
    public void createCategory(CategoryDTO categoryDTO) {
        Long userId = UserContext.getCurrentId();
        Merchant m = userRepository.findDetailedByUserId(userId)
                        .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND))
                        .getMerchant();

        if (categoryRepository.existsByMerchantAndName(m, categoryDTO.getName())) {
            throw new CategoryAlreadyExistException(CATEGORY_ALREADY_EXIST);
        }

        Category category = mapper.toCategory(categoryDTO);
        category.setMerchant(m); //optional = false, 一定要set

        categoryRepository.save(category);
    }

    @Override
    public Result<CategoryVO> getCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(CATEGORY_NOT_FOUND));

        CategoryVO categoryVO = mapper.toVO(category);
        return Result.success(categoryVO);
    }

    @Override
    @Transactional
    public void updateCategory(Long categoryId, CategoryDTO categoryDTO) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(CATEGORY_NOT_FOUND));

        mapper.updateCategoryFromDTO(categoryDTO,  category);

        categoryRepository.save(category);
    }

    @Override
    @Transactional
    public void updateCategoryStatus(Long categoryId, Short status) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(CATEGORY_NOT_FOUND));
        category.setStatus(status);
    }
}
