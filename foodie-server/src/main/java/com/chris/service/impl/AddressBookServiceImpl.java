package com.chris.service.impl;

import com.chris.dto.AddressBookDTO;
import com.chris.entity.AddressBook;
import com.chris.entity.Client;
import com.chris.exception.AddressBookNotFoundException;
import com.chris.exception.UserNotFoundException;
import com.chris.mapper.AddressBookMapper;
import com.chris.repository.AddressBookRepository;
import com.chris.repository.UserRepository;
import com.chris.service.AddressBookService;
import com.chris.vo.ClientAddressBookVO;
import com.chris.vo.resultVOs.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.chris.constant.MessageConstant.ADDRESS_NOT_FOUND;
import static com.chris.constant.MessageConstant.USER_NOT_FOUND;

@Service
public class AddressBookServiceImpl implements AddressBookService {
    @Autowired
    private AddressBookRepository addressBookRepository;

    @Autowired
    private AddressBookMapper addressBookMapper;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Result<List<ClientAddressBookVO>> listAll(Long userId) {
        List<AddressBook> list = addressBookRepository.findAllByClient_User_UserId(userId);
        return Result.success(addressBookMapper.toVO(list));
    }

    @Override
    @Transactional
    public void add(Long userId, AddressBookDTO dto) {
        Client c = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND))
                .getClient();
        AddressBook a = addressBookMapper.toEntity(dto);
        a.setClient(c);

        // 检查该用户是否已有默认地址，没有则新地址自动设为默认
        boolean hasDefault = addressBookRepository.existsByClient_User_UserIdAndIsDefaultTrue(userId);
        if (!hasDefault) {
            a.setIsDefault(true);
        }

        addressBookRepository.save(a);
    }

    @Override
    @Transactional
    public void update(Long userId, Long addressId, AddressBookDTO dto) {
        AddressBook ab = addressBookRepository.findById(addressId)
                .filter(x -> x.getClient().getUser().getUserId().equals(userId))
                .orElseThrow(() -> new AddressBookNotFoundException(ADDRESS_NOT_FOUND));
        addressBookMapper.updateAddressBookFromDTO(dto, ab);
    }

    @Override
    @Transactional
    public void delete(Long userId, Long[] addressId) {
        List<AddressBook> list = addressBookRepository
                .findAllByAddressIdInAndClient_User_UserId(List.of(addressId), userId);

        boolean deletingDefault = list.stream().anyMatch(AddressBook::getIsDefault);
        addressBookRepository.deleteAll(list);

        // 如果删除了默认地址，需要自动指定一个默认
        if (deletingDefault) {
            List<AddressBook> remain = addressBookRepository.findAllByClient_User_UserId(userId);
            if (!remain.isEmpty()) {
                // 假设设最新的为默认，也可以随机/业务定制
                AddressBook newDefault = remain.get(0);
                addressBookRepository.setDefaultByAddressId(newDefault.getAddressId());
            }
        }
    }

    @Override
    public Result<ClientAddressBookVO> get(Long userId, Long addressId) {
        AddressBook addressBook = addressBookRepository.findById(addressId)
                .filter(a -> a.getClient().getUser().getUserId().equals(userId))
                .orElseThrow(() -> new AddressBookNotFoundException(ADDRESS_NOT_FOUND));
        return Result.success(addressBookMapper.toVO(addressBook));
    }

    @Override
    @Transactional
    public void setDefault(Long userId, Long addressId) {
        // 1. 全部置为 isDefault=false
        addressBookRepository.resetDefaultByUserId(userId);
        // 2. 目标地址置为 isDefault=true
        addressBookRepository.setDefaultByAddressId(addressId);
        // 有唯一索引兜底，防止同时多条为 true
    }
}
