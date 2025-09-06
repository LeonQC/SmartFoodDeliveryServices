package com.chris.controller.rider;

import com.chris.context.UserContext;
import com.chris.dto.RiderLocationDTO;
import com.chris.service.RiderService;
import com.chris.vo.resultVOs.Result;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rider")
public class RiderStatusController {
    @Autowired
    private RiderService riderService;

    @GetMapping("/status")
    @Operation(summary = "Check Merchant Status", description = "Check the status of current login merchant")
    public Result<Short> getStatus() {
        Long userId = UserContext.getCurrentId();
        return riderService.getStatus(userId);
    }

    @PostMapping("/status/{newStatus}")
    @Operation(summary = "Change Merchant Status", description = "Change the status of current login merchant")
    public Result<String> changeStatus(@PathVariable Short newStatus) {
        Long userId = UserContext.getCurrentId();
        riderService.changeStatus(userId, newStatus);
        String status = newStatus == 1 ? "Rider is accepting orders" : "Rider is inactive";
        return Result.success(status);
    }

    @PostMapping("/location")
    @Operation(summary = "Update Rider Location", description = "Update the location of current login rider")
    public Result<String> updateLocation(@RequestBody RiderLocationDTO dto) {
        Long userId = UserContext.getCurrentId();
        riderService.updateLocation(userId, dto);
        return Result.success("Location updated");
    }
}
