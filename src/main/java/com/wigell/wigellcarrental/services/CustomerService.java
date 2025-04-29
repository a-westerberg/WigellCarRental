package com.wigell.wigellcarrental.services;

import com.wigell.wigellcarrental.models.entities.Customer;

import java.util.List;

//SA
public interface CustomerService {
    List<Customer> getAllCustomers();

    // WIG-27-SJ
    Customer getCustomerById(Long id);

    //WIG-30-SJ
    public String removeCustomerById(Long id);

    // WIG-29-SJ
    Customer updateCustomer(Customer customer);
}
