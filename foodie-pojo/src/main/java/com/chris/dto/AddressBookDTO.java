package com.chris.dto;

import lombok.Data;

@Data
public class AddressBookDTO {
    private String label;
    private String recipient;
    private String phone;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String zipcode;
    private String country;
}
