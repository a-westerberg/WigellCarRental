package com.wigell.wigellcarrental.services;

import com.wigell.wigellcarrental.entities.Customer;
import com.wigell.wigellcarrental.entities.Order;
import com.wigell.wigellcarrental.exceptions.ResourceNotFoundException;
import com.wigell.wigellcarrental.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

//SA
@Service
public class CustomerServiceImpl implements CustomerService{

    //SA
    private CustomerRepository customerRepository;

    //SA
    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    //SA
    @Override
    public List<Customer> getAllCustomers() {
        if(customerRepository.findAll().isEmpty()) {
            throw new ResourceNotFoundException("List","customers",0);
        }
        return customerRepository.findAll();
    }

    // WIG-27-SJ
    @Override
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Customer","id",id));
    }

    @Override
    public String removeCustomerById(Long id) {
        Customer customerToRemove = customerRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("Customer","id",id));

        List<Order> ordersToRemove = customerToRemove.getOrder();
        if (ordersToRemove != null) {
            //TODO Ska Orders CustomerId kunna vara Null?
        }

        return "Customer " + customerToRemove.getPersonalIdentityNumber() + " has been deleted.";
    }
}
