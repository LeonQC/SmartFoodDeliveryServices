package com.chris.controller.client;

import com.chris.context.UserContext;
import com.chris.dto.AddressBookDTO;
import com.chris.service.AddressBookService;
import com.chris.vo.ClientAddressBookVO;
import com.chris.vo.resultVOs.Result;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/client/address")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    @GetMapping
    @Operation(summary = "Get all address", description = "Get all address from address book of current client")
    public Result<List<ClientAddressBookVO>> listAll() {
        Long userId = UserContext.getCurrentId();
        return addressBookService.listAll(userId);
    }

    @PostMapping
    @Operation(summary = "Add address", description = "Add address to address book of current client")
    public Result<String> add(@RequestBody AddressBookDTO dto) {
        Long userId = UserContext.getCurrentId();
        addressBookService.add(userId, dto);
        return Result.success("Address added successfully");
    }

    @PutMapping("/{addressId}")
    @Operation(summary = "Update address", description = "Update address in address book of current client")
    public Result<String> update(@PathVariable Long addressId, @RequestBody AddressBookDTO dto) {
        Long userId = UserContext.getCurrentId();
        addressBookService.update(userId, addressId, dto);
        return Result.success("Address updated successfully");
    }

    @DeleteMapping
    @Operation(summary = "Delete address", description = "Delete address in address book of current client")
    public Result<String> delete(@RequestParam Long[] addressId) {
        Long userId = UserContext.getCurrentId();
        addressBookService.delete(userId, addressId);
        return Result.success("Address deleted successfully");
    }

    @GetMapping("/{addressId}")
    @Operation(summary = "Get address", description = "Get address in address book of current client")
    public Result<ClientAddressBookVO> get(@PathVariable Long addressId) {
        Long userId = UserContext.getCurrentId();
        return addressBookService.get(userId, addressId);
    }

    @PostMapping("{addressId}/setDefault")
    @Operation(summary = "Set default address", description = "Set default address in address book of current client")
    public Result<String> setDefault(@PathVariable Long addressId) {
        Long userId = UserContext.getCurrentId();
        addressBookService.setDefault(userId, addressId);
        return Result.success("success");
    }
}
