package com.chris.service;

import com.chris.dto.AddressBookDTO;
import com.chris.vo.ClientAddressBookVO;
import com.chris.vo.resultVOs.Result;

import java.util.List;

public interface AddressBookService {
    Result<List<ClientAddressBookVO>> listAll(Long userId);

    void add(Long userId, AddressBookDTO dto);

    void update(Long userId, Long addressId, AddressBookDTO dto);

    void delete(Long userId, Long[] addressId);

    Result<ClientAddressBookVO> get(Long userId, Long addressId);

    void setDefault(Long userId, Long addressId);

}
