package com.wigell.wigellcarrental.services;

import com.wigell.wigellcarrental.entities.Customer;
import com.wigell.wigellcarrental.entities.Order;

import java.security.Principal;
import java.util.List;

//SA
public interface CustomerService {
    List<Customer> getAllCustomers();

    // WIG-27-SJ
    Customer getCustomerById(Long id);

    List<Order> getOrders(Principal principal); //WIG-22-AA
}
