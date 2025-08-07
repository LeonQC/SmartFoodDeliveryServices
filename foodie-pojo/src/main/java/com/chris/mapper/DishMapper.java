package com.chris.mapper;

import com.chris.dto.DishPayloadDTO;
import com.chris.entity.Dish;
import com.chris.vo.DishVO;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = { CategoryMapper.class })
public interface DishMapper {

    /** 用于查询 */
    @Mapping(source = "category", target = "categoryVO")
    DishVO toVO(Dish dish);

    /** 用于新建 */
    @Mapping(target="category", ignore=true)
    @Mapping(target="status", constant = "1")
    @Mapping(target="image", ignore = true)
    Dish toDish(DishPayloadDTO dto);

    /** 用于更新 */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target="category", ignore=true)
    @Mapping(target = "updateTime", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target="image", ignore = true)
    void updateDishFromDTO(DishPayloadDTO dto, @MappingTarget Dish dish);

    /** 用户查询时使用 */
    /** 列表查询：不包含 categoryVO */
    @Named("toVOWithoutCategory")
    @Mapping(target = "categoryVO", ignore = true)
    DishVO toVOWithoutCategory(Dish dish);

    /** 同样也可以批量不带 category 的版本 */
    @IterableMapping(qualifiedByName = "toVOWithoutCategory")
    List<DishVO> toVOListWithoutCategory(List<Dish> dishes);
}
