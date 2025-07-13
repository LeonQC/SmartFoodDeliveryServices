package com.chris.mapper;

import com.chris.entity.Merchant;
import com.chris.vo.AllRestaurantsVO;
import com.chris.vo.RestaurantVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = CategoryMapper.class)
public interface RestaurantMapper {

    @Mapping(target="distance",      ignore = true)
    AllRestaurantsVO toAllRestaurantsVO(Merchant merchant);


    @Mapping(source = "category", target = "categories")
    RestaurantVO toRestaurantVO(Merchant merchant);
}
