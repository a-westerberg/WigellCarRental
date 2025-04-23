package com.wigell.wigellcarrental.services;

import com.wigell.wigellcarrental.entities.Customer;

import java.util.List;

//SA
public interface CustomerService {
    List<Customer> getAllCustomers();

    // WIG-27-SJ
    Customer getCustomerById(Long id);
}
