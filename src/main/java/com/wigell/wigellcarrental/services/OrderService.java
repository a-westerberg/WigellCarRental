package com.wigell.wigellcarrental.services;

import com.wigell.wigellcarrental.entities.Order;

import java.util.List;

//SA
public interface OrderService {
    List<Order> getActiveOrders();
    List<Order> getActiveOrdersForCustomer(String personalIdentityNumber);
}
