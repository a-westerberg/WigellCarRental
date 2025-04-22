package com.wigell.wigellcarrental.services;

import com.wigell.wigellcarrental.entities.Order;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

//SA
public interface OrderService {
    List<Order> getActiveOrders();//SA
    List<Order> getActiveOrdersForCustomer(String personalIdentityNumber);//AWS
    String cancelOrder(Long orderId, Principal principal);//SA

    String removeOrdersBeforeDate(LocalDate date, Principal principal);
}
