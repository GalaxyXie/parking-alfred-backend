
package com.alfred.parkingalfred.service.impl;

import com.alfred.parkingalfred.dto.CreateOrderDto;
import com.alfred.parkingalfred.entity.Order;
import com.alfred.parkingalfred.enums.OrderStatusEnum;
import com.alfred.parkingalfred.enums.ResultEnum;
import com.alfred.parkingalfred.exception.OrderNotExistedException;
import com.alfred.parkingalfred.exception.SecKillOrderException;
import com.alfred.parkingalfred.repository.OrderRepository;
import com.alfred.parkingalfred.service.OrderService;
import com.alfred.parkingalfred.utils.RedisLock;
import com.alfred.parkingalfred.utils.UUIDUtil;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

  @Autowired
  private RedisLock redisLock;
  private static final int TIMEOUT = 10 * 1000;//超时时间 10s

  private final OrderRepository orderRepository;

  public OrderServiceImpl(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  @Override
  public Order addOrder(CreateOrderDto createOrderDto) {

    Order order = mapToOrder(createOrderDto);
    return orderRepository.save(order);
  }

  @Override
  public Order getOrderById(Long id) {
    return orderRepository.findById(id)
        .orElseThrow(() -> new OrderNotExistedException(ResultEnum.RESOURCES_NOT_EXISTED));
  }

  @Override
  public Order updateOrderStatusById(Long id, Order order) {
    //lock
    long time = System.currentTimeMillis() + TIMEOUT;
    if (!redisLock.lock(id.toString(), String.valueOf(time))) {
      throw new SecKillOrderException(ResultEnum.RESOURCES_NOT_EXISTED);
    }
    Order orderFinded = orderRepository.findById(id)
        .orElseThrow(() -> new OrderNotExistedException(ResultEnum.RESOURCES_NOT_EXISTED));
    Integer statusResult = orderFinded.getStatus();
    if (order.getStatus().equals(OrderStatusEnum.CONFIRM)){
      this.updateOrder(orderFinded,order);
    }else{
      if (statusResult.equals(OrderStatusEnum.WAIT_FOR_CONFIRM)) {
        throw new SecKillOrderException(ResultEnum.RESOURCES_NOT_EXISTED);
      } else {
        this.updateOrder(orderFinded,order);
      }
      //unlock
      redisLock.unlock(id.toString(), String.valueOf(time));
    }
    return orderRepository.save(orderFinded);
  }

  @Override
  public List<Order> getOrders() {
    return orderRepository.findAll();
  }

  private Order mapToOrder(CreateOrderDto createOrderDto) {
    Order order = new Order();
    order.setCarNumber(createOrderDto.getCarNumber());
    order.setType(createOrderDto.getType());
    order.setCustomerAddress(createOrderDto.getCustomerAddress());
    order.setReservationTime(createOrderDto.getReservationTime());
    order.setStatus(OrderStatusEnum.WAIT_FOR_RECEIVE.getCode());
    order.setOrderId(UUIDUtil.generateUUID());
    return order;
  }

  @Override
  public List<Order> getOrdersByStatus(Integer status) {
    return orderRepository.findOrdersByStatus(status);
  }

  private void updateOrder(Order orderFinded, Order order){
      if (order.getType() != null) {
          orderFinded.setStatus(order.getType());
      }
      if (order.getReservationTime() != null) {
          orderFinded.setReservationTime(order.getReservationTime());
      }
      if (order.getCustomerAddress() != null) {
          orderFinded.setCustomerAddress(order.getCustomerAddress());
      }
      if (order.getStatus() != null) {
          orderFinded.setStatus(order.getStatus());
      }
      if (order.getEmployee() != null) {
          orderFinded.setEmployee(order.getEmployee());
      }
      if (order.getCarNumber() != null) {
          orderFinded.setCarNumber(order.getCarNumber());
      }
      if (order.getParkingLot() != null) {
          orderFinded.setParkingLot(order.getParkingLot());
      }
  }
}
