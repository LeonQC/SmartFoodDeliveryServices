package com.chris.mapper;

import com.chris.dto.CategoryDTO;
import com.chris.entity.Category;
import com.chris.vo.CategoryVO;
import org.mapstruct.*;


@Mapper(componentModel = "spring")
public interface CategoryMapper {
    /** 用于新建 */
    @Mapping(target="merchant", ignore=true)
    @Mapping(target="dishes", ignore=true)
    @Mapping(constant = "1", target = "status")
    Category toCategory(CategoryDTO dto);

    /** 用于更新：只覆盖 DTO 非 null 字段，且不改 merchant/dishes */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target="merchant", ignore=true)
    @Mapping(target="dishes",   ignore=true)
    @Mapping(target = "updateTime", expression = "java(java.time.LocalDateTime.now())")
    void updateCategoryFromDTO(CategoryDTO dto, @MappingTarget Category category);

    /** 用于查询 */
    CategoryVO toVO(Category category);
}
