package com.wigell.wigellcarrental.services;

import com.wigell.wigellcarrental.entities.Order;
import com.wigell.wigellcarrental.exceptions.ResourceNotFoundException;
import com.wigell.wigellcarrental.repositories.OrderRepository;
import com.wigell.wigellcarrental.entities.Order;
import com.wigell.wigellcarrental.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
//SA
@Service
public class OrderServiceImpl implements OrderService{
    //AWS
    private final OrderRepository orderRepository;
    //AWS
    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
    //AWS
    @Override
    public List<Order> getActiveOrdersForCustomer(String personalIdentityNumber) {
        return orderRepository.findByCustomer_PersonalIdentityNumberAndIsActiveTrue(personalIdentityNumber);
    }

    //SA
    @Override
    public List<Order> getActiveOrders() {
        if(orderRepository.findAllByIsActiveTrue().isEmpty()){
            throw new ResourceNotFoundException("List","active orders",0);
        }
        return orderRepository.findAllByIsActiveTrue();
    }
}
