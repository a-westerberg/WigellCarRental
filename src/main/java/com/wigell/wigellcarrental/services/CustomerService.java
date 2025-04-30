package com.wigell.wigellcarrental.services;

import com.wigell.wigellcarrental.models.entities.Customer;
import com.wigell.wigellcarrental.models.entities.Order;

import java.security.Principal;
import java.util.List;

//SA
public interface CustomerService {
    List<Customer> getAllCustomers();

    // WIG-27-SJ
    Customer getCustomerById(Long id);

    //WIG-30-SJ
    public String removeCustomerById(Long id);

    List<Order> getOrders(Principal principal); //WIG-22-AA

    // WIG-29-SJ
    Customer updateCustomer(Customer customer);

    // WIG-23-AWS
    Customer addCustomer(Customer customer, Principal principal);
}
