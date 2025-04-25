package com.wigell.wigellcarrental.services;

import com.wigell.wigellcarrental.entities.Customer;
import com.wigell.wigellcarrental.entities.Order;
import com.wigell.wigellcarrental.exceptions.ResourceNotFoundException;
import com.wigell.wigellcarrental.repositories.CustomerRepository;
import com.wigell.wigellcarrental.repositories.OrderRepository;
import com.wigell.wigellcarrental.services.utilities.MicroMethods;
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

    // WIG-29-SJ
    @Override
    public Customer updateCustomer(Customer customer) {
        Customer updatedCustomer = validateCustomer(customer);
        return customerRepository.save(updatedCustomer);
    }

    public Customer validateCustomer(Customer customer) {
        Customer existingCustomer = customerRepository.findById(customer.getId()).orElseThrow(()->
                new ResourceNotFoundException("Customer","id",customer.getId()));

        if (MicroMethods.validateForUpdate(customer.getFirstName())) {
            existingCustomer.setFirstName(customer.getFirstName());
        }
        if (MicroMethods.validateForUpdate(customer.getLastName())) {
            existingCustomer.setLastName(customer.getLastName());
        }
        /*
        // WIG-29-SJ
        // If values of Email & Phone needs to be unique. If not, remove code later.
        if (MicroMethods.validateForUpdate(customer.getEmail())) {
            MicroMethods.validateUniqueValue("email", customer.getEmail(), customerRepository::existsByEmail);
            existingCustomer.setEmail(customer.getEmail());
        }

        if (MicroMethods.validateForUpdate(customer.getPhoneNumber())) {
            MicroMethods.validateUniqueValue("phoneNumber", customer.getPhoneNumber(), customerRepository::existsByPhoneNumber);
            existingCustomer.setPhoneNumber(customer.getPhoneNumber());
        }
        */

        if (MicroMethods.validateForUpdate(customer.getEmail())) {
            existingCustomer.setEmail(customer.getEmail());
        }
        if (MicroMethods.validateForUpdate(customer.getPhoneNumber())) {
            existingCustomer.setPhoneNumber(customer.getPhoneNumber());
        }
        if (MicroMethods.validateForUpdate(customer.getAddress())) {
            existingCustomer.setAddress(customer.getAddress());
        }

        return existingCustomer;
    }


    //WIG-22-AA
    public List<Order> getOrders(Principal principal) {
        LocalDate today = LocalDate.now();
        Optional<Customer> customer = customerRepository.findByPersonalIdentityNumber(principal.getName()/*"19751230-9101"*/);
        if(customer.isPresent()){
            return orderRepository.findByCustomer_PersonalIdentityNumberAndEndDateBefore(customer.get().getPersonalIdentityNumber(), today);
        }
        throw new ResourceNotFoundException("Customer", "user", principal.getName());
    }
}
