package com.chris.service;

import com.chris.dto.CartItemDTO;
import com.chris.vo.ShoppingCartVO;
import com.chris.vo.resultVOs.Result;

import java.util.List;

public interface ShoppingCartService {
    void addItem(Long userId, CartItemDTO dto);

    Result<List<ShoppingCartVO>> listItems(Long userId);

    void updateItem(Long userId, Long cartId, CartItemDTO dto);

    void removeItem(Long userId, Long[] cartIdList);

    void clearCart(Long userId);
}
