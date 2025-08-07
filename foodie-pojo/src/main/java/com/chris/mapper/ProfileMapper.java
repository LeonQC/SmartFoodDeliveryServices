package com.chris.mapper;

import com.chris.dto.ProfileUpdateDTO;
import com.chris.vo.profileVOs.MerchantVO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.chris.entity.User;
import com.chris.entity.Merchant;
import com.chris.entity.Client;
import com.chris.entity.Rider;
import com.chris.vo.profileVOs.ProfileVO;
import com.chris.vo.profileVOs.ClientVO;
import com.chris.vo.profileVOs.RiderVO;
import org.mapstruct.MappingTarget;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    @Mapping(expression = "java(user.getRole().name())", target = "role")
    @Mapping(expression = "java(user.getClient() != null ? toClientVO(user.getClient()) : null)", target = "client")
    @Mapping(expression = "java(user.getMerchant() != null ? toMerchantVO(user.getMerchant()) : null)", target = "merchant")
    @Mapping(expression = "java(user.getRider() != null ? toRiderVO(user.getRider()) : null)", target = "rider")
    ProfileVO toProfileVO(User user);

    MerchantVO toMerchantVO(Merchant merchant);
    ClientVO   toClientVO(Client client);
    RiderVO    toRiderVO(Rider rider);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    @Mapping(target = "profileCompleted", constant = "true")
    void updateUserFromDto(ProfileUpdateDTO dto, @MappingTarget User user);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    @Mapping(target = "merchantImage", ignore = true)
    void updateMerchantFromDto(ProfileUpdateDTO dto, @MappingTarget Merchant merchant);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    @Mapping(target = "avatar", ignore = true)
    void updateClientFromDto(ProfileUpdateDTO dto, @MappingTarget Client client);

    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    @Mapping(target = "avatar", ignore = true)
    void updateRiderFromDto(ProfileUpdateDTO dto, @MappingTarget Rider rider);
}