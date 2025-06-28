package com.chris.vo.orderDetailVOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressBookVO {
    private String recipient;
    private String phone;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
}
