package com.chris.mapper;

import com.chris.dto.DishPayloadDTO;
import com.chris.entity.Dish;
import com.chris.vo.DishVO;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = { CategoryMapper.class })
public interface DishMapper {

    /** 用于查询 */
    @Mapping(source = "category", target = "categoryVO")
    DishVO toVO(Dish dish);

    /** 用于新建 */
    @Mapping(target="category", ignore=true)
    @Mapping(target="status", constant = "1")
    Dish toDish(DishPayloadDTO dto);

    /** 用于更新 */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target="category", ignore=true)
    @Mapping(target = "updateTime", expression = "java(java.time.LocalDateTime.now())")
    void updateDishFromDTO(DishPayloadDTO dto, @MappingTarget Dish dish);
}
