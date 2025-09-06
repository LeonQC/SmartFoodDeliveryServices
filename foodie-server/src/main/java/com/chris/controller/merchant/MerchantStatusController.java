package com.chris.controller.merchant;

import com.chris.context.UserContext;
import com.chris.service.MerchantService;
import com.chris.vo.resultVOs.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/merchant")
@Tag(name = "StatusController", description = "Status-related APIs")
public class MerchantStatusController {
    @Autowired
    private MerchantService merchantService;

    @GetMapping("/status")
    @Operation(summary = "Check Merchant Status", description = "Check the status of current login merchant")
    public Result<Short> getStatus() {
        Long userId = UserContext.getCurrentId();
        return merchantService.getStatus(userId);
    }

    @PostMapping("/status/{newStatus}")
    @Operation(summary = "Change Merchant Status", description = "Change the status of current login merchant")
    public Result<String> changeStatus(@PathVariable Short newStatus) {
        Long userId = UserContext.getCurrentId();
        merchantService.changeStatus(userId, newStatus);
        String status = newStatus == 1 ? "Restaurant is opening" : "Restaurant is closed";
        return Result.success(status);
    }
}
