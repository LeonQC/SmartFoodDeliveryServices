package com.chris.controller.client;

import com.chris.service.DeliveryFeeService;
import com.chris.vo.DeliveryFeeVO;
import com.chris.vo.resultVOs.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/client/deliveryFee")
public class DeliveryFeeController {
    @Autowired
    private DeliveryFeeService deliveryFeeService;

    @GetMapping
    public Result<DeliveryFeeVO> calcFee(@RequestParam Long merchantId, @RequestParam Long addressId) {
        return deliveryFeeService.calcFee(merchantId, addressId);
    }
}
