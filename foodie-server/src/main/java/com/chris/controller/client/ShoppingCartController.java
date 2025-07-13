package com.chris.controller.client;

import com.chris.context.UserContext;
import com.chris.dto.CartItemDTO;
import com.chris.service.ShoppingCartService;
import com.chris.vo.ShoppingCartVO;
import com.chris.vo.resultVOs.Result;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/client/cart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService cartService;

    @PostMapping
    @Operation(summary = "Add item to cart")
    public Result<String> add(@RequestBody CartItemDTO dto) {
        Long userId = UserContext.getCurrentId();
        cartService.addItem(userId, dto);
        return Result.success("Item added to cart");
    }

    @GetMapping
    @Operation(summary = "List cart items")
    public Result<List<ShoppingCartVO>> list() {
        Long userId = UserContext.getCurrentId();
        return cartService.listItems(userId);
    }

    @PutMapping("/{cartId}")
    @Operation(summary = "Update cart item quantity/remark")
    public Result<String> update(
            @PathVariable Long cartId,
            @RequestBody CartItemDTO dto) {
        Long userId = UserContext.getCurrentId();
        cartService.updateItem(userId, cartId, dto);
        return Result.success("Success");
    }

    @DeleteMapping("/remove")
    @Operation(summary = "Remove multiple  cart items")
    public Result<String> remove(@RequestParam Long[] cartIdList) {
        Long userId = UserContext.getCurrentId();
        cartService.removeItem(userId, cartIdList);
        return Result.success("Success");
    }

    @DeleteMapping("/clear")
    @Operation(summary = "Clear entire cart")
    public Result<String> clear() {
        Long userId = UserContext.getCurrentId();
        cartService.clearCart(userId);
        return Result.success("Shopping cart is cleared");
    }
}
