package com.chris.mapper;

import com.chris.entity.Order;
import com.chris.entity.AddressBook;
import com.chris.entity.OrderItem;
import com.chris.entity.OrderStatusLog;
import com.chris.vo.orderDetailVOs.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderDetailMapper {

    @Mapping(target = "clientPhone", source = "client.phone")
    MerchantOrderDetailVO toMerchantOrderDetailVO(Order order);

    @Mapping(target = "merchantName", source = "merchant.merchantName")
    @Mapping(target = "merchantPhone", source = "merchant.phone")
    ClientOrderDetailVO toClientOrderDetailVO(Order order);

    @Mapping(target = "dishName", source = "dish.name")
    OrderItemVO toOrderItemVO(OrderItem item);

    List<OrderItemVO> toOrderItemVOs(List<OrderItem> items);

    AddressBookVO toAddressBookVO(AddressBook address);

    OrderStatusLogVO toOrderStatusLogVO(OrderStatusLog log);

    List<OrderStatusLogVO> toOrderStatusLogVOs(List<OrderStatusLog> logs);
}
