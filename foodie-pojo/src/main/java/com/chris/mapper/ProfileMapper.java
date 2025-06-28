package com.chris.mapper;

import com.chris.dto.ProfileUpdateDTO;
import com.chris.vo.MerchantVO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.chris.entity.User;
import com.chris.entity.Merchant;
import com.chris.entity.Client;
import com.chris.entity.Rider;
import com.chris.vo.ProfileVO;
import com.chris.vo.ClientVO;
import com.chris.vo.RiderVO;
import org.mapstruct.MappingTarget;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    @Mapping(expression = "java(user.getRole().name())", target = "role")
    ProfileVO toProfileVO(User user);

    MerchantVO toMerchantVO(Merchant merchant);
    ClientVO   toClientVO(Client client);
    RiderVO    toRiderVO(Rider rider);

    // 新增：DTO 更新到 User
    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    @Mapping(target = "profileCompleted", constant = "true")
    void updateUserFromDto(ProfileUpdateDTO dto, @MappingTarget User user);

    // 新增：DTO 更新到 Merchant（忽略 null 字段）
    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    void updateMerchantFromDto(ProfileUpdateDTO dto, @MappingTarget Merchant merchant);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    void updateClientFromDto(ProfileUpdateDTO dto, @MappingTarget Client client);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    void updateRiderFromDto(ProfileUpdateDTO dto, @MappingTarget Rider rider);
}
