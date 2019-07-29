package com.alfred.parkingalfred.service;

import com.alfred.parkingalfred.dto.CreateOrderDto;
import com.alfred.parkingalfred.entity.Order;

import java.util.List;

public interface OrderService {

    List<Order> getOrders(String sortProperty, String sortOrder, Integer filterStatus);

    Order addOrder(CreateOrderDto createOrderDto);

    Order getOrderById(Long id);

    Order updateOrderStatusById(Long id, Order order);
}
