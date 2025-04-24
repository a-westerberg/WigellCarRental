package com.wigell.wigellcarrental.services;

import com.wigell.wigellcarrental.entities.Customer;
import com.wigell.wigellcarrental.entities.Order;
import com.wigell.wigellcarrental.exceptions.ResourceNotFoundException;
import com.wigell.wigellcarrental.repositories.CustomerRepository;
import com.wigell.wigellcarrental.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

//SA
@Service
public class CustomerServiceImpl implements CustomerService{

    //SA
    private CustomerRepository customerRepository;
    //AA
    private final OrderRepository orderRepository;

    //SA //AA
    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository, OrderRepository orderRepository) {
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
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

    //WIG-22-AA
    public List<Order> getOrders(Principal principal) {
        LocalDate today = LocalDate.now();
        Optional<Customer> customer = customerRepository.findByPersonalIdentityNumber(principal.getName());
        if(customer.isPresent()){
            return orderRepository.findByCustomer_PersonalIdentityNumberAndEndDateBefore(customer.get().getPersonalIdentityNumber(), today);
        }
        throw new ResourceNotFoundException("Customer", "user", principal.getName());
    }
}
