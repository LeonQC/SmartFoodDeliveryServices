package com.chris.mapper;

import com.chris.entity.Order;
import com.chris.entity.AddressBook;
import com.chris.entity.OrderItem;
import com.chris.entity.OrderStatusLog;
import com.chris.vo.orderDetailVOs.AddressBookVO;
import com.chris.vo.orderDetailVOs.OrderDetailVO;
import com.chris.vo.orderDetailVOs.OrderItemVO;
import com.chris.vo.orderDetailVOs.OrderStatusLogVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderDetailMapper {

    @Mapping(target = "clientPhone", source = "client.phone")
    OrderDetailVO toOrderDetailVO(Order order);

    @Mapping(target = "dishName", source = "dish.name")
    OrderItemVO toOrderItemVO(OrderItem item);

    List<OrderItemVO> toOrderItemVOs(List<OrderItem> items);

    AddressBookVO toAddressBookVO(AddressBook address);

    OrderStatusLogVO toOrderStatusLogVO(OrderStatusLog log);

    List<OrderStatusLogVO> toOrderStatusLogVOs(List<OrderStatusLog> logs);
}
