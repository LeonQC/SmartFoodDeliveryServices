package com.chris.mapper;

import com.chris.dto.AddressBookDTO;
import com.chris.entity.AddressBook;
import com.chris.vo.ClientAddressBookVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AddressBookMapper {

    /** 用于查询 */
    ClientAddressBookVO toVO(AddressBook addressBook);
    List<ClientAddressBookVO> toVO(List<AddressBook> addressBooks);

    /** 用于新建 */
    @Mapping(target = "isDefault", constant = "false")
    AddressBook toEntity(AddressBookDTO addressBookDTO);

    /** 用于更新 */
    @Mapping(target = "updateTime", expression = "java(java.time.LocalDateTime.now())")
    void updateAddressBookFromDTO(AddressBookDTO dto, @MappingTarget AddressBook addressBook);
}
