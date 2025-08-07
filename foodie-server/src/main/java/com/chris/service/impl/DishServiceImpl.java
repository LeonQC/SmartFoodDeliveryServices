package com.chris.service.impl;

import com.chris.dto.DishPayloadDTO;
import com.chris.dto.DishQueryDTO;
import com.chris.entity.Category;
import com.chris.entity.Dish;
import com.chris.exception.CategoryNotFoundException;
import com.chris.exception.DishNotFoundException;
import com.chris.mapper.DishMapper;
import com.chris.repository.CategoryRepository;
import com.chris.repository.DishRepository;
import com.chris.service.DishService;
import com.chris.vo.dashboardVOs.DashboardCategoryDishStatusVO;
import com.chris.vo.DishVO;
import com.chris.vo.resultVOs.PageResult;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.chris.constant.MessageConstant.CATEGORY_NOT_FOUND;
import static com.chris.constant.MessageConstant.DISH_NOT_FOUND;

@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishRepository dishRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private S3ServiceImpl s3Service;

    @Override
    @Transactional(readOnly = true)
    public PageResult<DishVO> getDishes(DishQueryDTO dto) {
        // 1. 构造 Pageable（注意 page 参数从 1 开始，要 -1）
        Pageable pageable = PageRequest.of(
                Math.max(dto.getPage() - 1, 0),
                dto.getPageSize(),
                Sort.by("dishId").ascending()
        );

        // 2. 构造 Specification
        Specification<Dish> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (dto.getName() != null && !dto.getName().isBlank()) {
                predicates.add(cb.like(root.get("name"), "%" + dto.getName().trim() + "%"));
            }
            if (dto.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), dto.getStatus()));
            }
            if (dto.getCategoryId() != null) {
                predicates.add(cb.equal(
                        root.get("category").get("categoryId"),
                        dto.getCategoryId()
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // 3. 执行查询
        Page<Dish> page = dishRepository.findAll(spec, pageable);

        // 4. 转 VO
        List<DishVO> vos = page.getContent()
                .stream()
                .map(dishMapper::toVO)
                .collect(Collectors.toList());

        // 5. 装配返回
        return new PageResult<>(page.getTotalElements(), vos);
    }

    @Override
    @Transactional
    public void deleteDishes(Long[] dishIds) {
        // 1. 删除S3中的图片
        List<Dish> dishes = dishRepository.findAllById(List.of(dishIds));
        for (Dish dish : dishes) {
            s3Service.deleteImage(dish.getImage());
        }
        // 2. 删除数据库中的dishes
        dishRepository.deleteAllById(List.of(dishIds));
    }

    @Override
    @Transactional
    public void createDish(DishPayloadDTO dto) {
        // 1. 映射
        Dish dish = dishMapper.toDish(dto);
        // 2. 校验
        Long categoryId = dto.getCategoryId();
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(CATEGORY_NOT_FOUND));
        // 3. 关联
        dish.setCategory(category);

        // 4. 持久化S3中的imageKey
        String permKey = s3Service.persistTemporaryImage(dto.getImage(), "Dishes");
        dish.setImage(permKey);

        // 5. 保存
        dishRepository.save(dish);
    }

    @Override
    @Transactional(readOnly = true)
    public DishVO getDish(Long dishId) {
        Dish dish = dishRepository.findById(dishId)
                .orElseThrow(() -> new DishNotFoundException(DISH_NOT_FOUND));
        return dishMapper.toVO(dish);
    }

    @Override
    @Transactional
    public void updateDish(Long dishId, DishPayloadDTO dto) {
        // 1. 根据dishId获取Dish
        Dish dish = dishRepository.findById(dishId)
                .orElseThrow(() -> new DishNotFoundException(DISH_NOT_FOUND));

        // 2. 判断所属类型是否更新
        if (!dish.getCategory().getCategoryId().equals(dto.getCategoryId())) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException(CATEGORY_NOT_FOUND));
            dish.setCategory(category);
        }

        // 3. 判断图片是否更新
        String oldKey  = dish.getImage();
        String tempKey = dto.getImage();
        if (tempKey != null && !tempKey.isBlank() && !tempKey.equals(oldKey)) {
            // 4. 删除旧的
            if (oldKey != null && !oldKey.isBlank()) {
                s3Service.deleteImage(oldKey);
            }
            // 5. 持久化新的
            String permKey = s3Service.persistTemporaryImage(tempKey, "Dishes");
            dish.setImage(permKey);
        }
        // 6. 映射其他字段
        dishMapper.updateDishFromDTO(dto, dish);
    }

    @Override
    @Transactional
    public void updateDishStatus(Long dishId, Short status) {
        Dish dish = dishRepository.findById(dishId)
                .orElseThrow(() -> new DishNotFoundException(DISH_NOT_FOUND));
        dish.setStatus(status);
    }

    @Override
    public DashboardCategoryDishStatusVO getCategoryDishStatus(Long userId) {
        // 查分类的总数&启用数
        long catTotal    = categoryRepository.countByMerchantUserUserId(userId);
        long catActive   = categoryRepository.countByMerchantUserUserIdAndStatus(userId, (short)1);
        // 查菜品的总数&启用数
        long dishTotal   = dishRepository.countByCategoryMerchantUserUserId(userId);
        long dishActive  = dishRepository.countByCategoryMerchantUserUserIdAndStatus(userId, (short)1);

        return new DashboardCategoryDishStatusVO(catTotal, catActive, dishTotal, dishActive);
    }
}
