package com.wigell.wigellcarrental.services;

import com.wigell.wigellcarrental.entities.Order;
import com.wigell.wigellcarrental.repositories.OrderRepository;
import org.springframework.stereotype.Service;


import java.util.List;
//SA
@Service
public class OrderServiceImpl implements OrderService{

    private OrderRepository orderRepository;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public List<Order> getActiveOrders() {
        if(orderRepository.findAllByIsActiveTrue().isEmpty()){
            //exception
        }
        return orderRepository.findAllByIsActiveTrue();
    }
}
