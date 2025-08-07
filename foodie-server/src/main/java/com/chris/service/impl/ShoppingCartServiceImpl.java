package com.chris.service.impl;

import com.chris.dto.CartItemDTO;
import com.chris.entity.Client;
import com.chris.entity.Dish;
import com.chris.entity.ShoppingCart;
import com.chris.exception.CartItemNotFoundException;
import com.chris.exception.DishNotFoundException;
import com.chris.exception.ExistedCartItemException;
import com.chris.exception.UserNotFoundException;
import com.chris.repository.DishRepository;
import com.chris.repository.ShoppingCartRepository;
import com.chris.repository.UserRepository;
import com.chris.service.ShoppingCartService;
import com.chris.vo.ShoppingCartVO;
import com.chris.vo.resultVOs.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static com.chris.constant.MessageConstant.*;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private DishRepository dishRepository;

    @Override
    @Transactional
    public void addItem(Long userId, CartItemDTO dto) {
        // 1) 先拿到 User，再取关联的 Client
        Client client = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND))
                .getClient();
        if (client == null) {
            throw new UserNotFoundException(USER_IS_NOT_CLIENT);
        }
        // 2) 校验菜品存在
        Dish dish = dishRepository.findById(dto.getDishId())
                .orElseThrow(() -> new DishNotFoundException(DISH_NOT_FOUND));
        // 3) 如果已在当前用户的购物车里 → 提示改用 update
        boolean exists = shoppingCartRepository
                .findByClient_User_UserIdAndDish_DishId(userId, dto.getDishId())
                .isPresent();
        if (exists) {
            throw new ExistedCartItemException(CART_ITEM_EXISTED);
        }
        // 4) 否则全新插入
        ShoppingCart sc = new ShoppingCart();
        sc.setClient(client);
        sc.setDish(dish);
        sc.setDishName(dish.getName());
        sc.setUnitPrice(dish.getPrice());
        sc.setQuantity(dto.getQuantity());
        shoppingCartRepository.save(sc);
    }

    @Override
    public Result<List<ShoppingCartVO>> listItems(Long userId) {
        List<ShoppingCartVO> vos = shoppingCartRepository
                .findByClient_User_UserId(userId)
                .stream()
                .map(sc -> {
                    ShoppingCartVO vo = new ShoppingCartVO();
                    BeanUtils.copyProperties(sc, vo);
                    vo.setDishId(sc.getDish().getDishId());
                    vo.setSubtotal(sc.getUnitPrice().multiply(BigDecimal.valueOf(sc.getQuantity())));
                    vo.setImage(sc.getDish().getImage());
                    return vo;
                })
                .collect(Collectors.toList());
        return Result.success(vos);
    }
    @Override
    @Transactional
    public void updateItem(Long userId, Long cartId, CartItemDTO dto) {
        ShoppingCart sc = shoppingCartRepository.findById(cartId)
                .filter(x -> x.getClient().getUser().getUserId().equals(userId))
                .orElseThrow(() -> new CartItemNotFoundException(CART_ITEM_NOT_FOUND));

        sc.setQuantity(dto.getQuantity());
        shoppingCartRepository.save(sc);
    }


    @Override
    @Transactional
    public void removeItem(Long userId, Long[] cartIdList) {
        // 1) 先拿到属于该 user 的、并且 ID 在列表里的所有项
        List<ShoppingCart> items = shoppingCartRepository
                .findAllByClient_User_UserIdAndCartIdIn(userId, List.of(cartIdList));
        // 2) 批量删除
        shoppingCartRepository.deleteAll(items);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        shoppingCartRepository.deleteByClient_User_UserId(userId);
    }
}
