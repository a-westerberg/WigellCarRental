package com.wigell.wigellcarrental.services;

import com.wigell.wigellcarrental.models.entities.Order;
import com.wigell.wigellcarrental.models.valueobjects.PopularBrandStats;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

//SA
public interface OrderService {
    List<Order> getActiveOrders();//SA
    List<Order> getActiveOrdersForCustomer(String personalIdentityNumber);//AWS
    String cancelOrder(Long orderId, Principal principal);//SA

    String removeOrdersBeforeDate(LocalDate date, Principal principal);
    Order addOrder(Order order, Principal principal); // WIG-28-SJ

    List<Order> getAllOrdersHistory();//SA

    String updateOrderStatus(Long orderId, String status, Principal principal);//SA

    String updateOrderCar(Long orderId, Long carId, Principal principal);//SA

    //WIG-85-AA
    PopularBrandStats getPopularBrand(String startDate, String endDate);
}
