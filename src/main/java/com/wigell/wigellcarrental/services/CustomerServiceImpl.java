package com.wigell.wigellcarrental.services;

import com.wigell.wigellcarrental.entities.Customer;
import com.wigell.wigellcarrental.entities.Order;
import com.wigell.wigellcarrental.exceptions.ConflictException;
import com.wigell.wigellcarrental.exceptions.ResourceNotFoundException;
import com.wigell.wigellcarrental.repositories.CustomerRepository;
import com.wigell.wigellcarrental.repositories.OrderRepository;
import com.wigell.wigellcarrental.services.utilities.MicroMethods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

//SA
@Service
public class CustomerServiceImpl implements CustomerService{

    private final OrderRepository orderRepository;
    //SA
    private CustomerRepository customerRepository;

    //SA
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

        if (MicroMethods.validateNotNull(customer.getFirstName())) {
            existingCustomer.setFirstName(customer.getFirstName());
        }
        if (MicroMethods.validateNotNull(customer.getLastName())) {
            existingCustomer.setLastName(customer.getLastName());
        }
        /*
        // WIG-29-SJ
        // If values of Email & Phone needs to be unique. If not, remove code later.
        if (MicroMethods.validateNotNull(customer.getEmail())) {
            MicroMethods.validateUniqueValue("email", customer.getEmail(), customerRepository::existsByEmail);
            existingCustomer.setEmail(customer.getEmail());
        }

        if (MicroMethods.validateNotNull(customer.getPhoneNumber())) {
            MicroMethods.validateUniqueValue("phoneNumber", customer.getPhoneNumber(), customerRepository::existsByPhoneNumber);
            existingCustomer.setPhoneNumber(customer.getPhoneNumber());
        }
        */

        if (MicroMethods.validateNotNull(customer.getEmail())) {
            existingCustomer.setEmail(customer.getEmail());
        }
        if (MicroMethods.validateNotNull(customer.getPhoneNumber())) {
            existingCustomer.setPhoneNumber(customer.getPhoneNumber());
        }
        if (MicroMethods.validateNotNull(customer.getAddress())) {
            existingCustomer.setAddress(customer.getAddress());
        }

        return existingCustomer;
    }


    // WIG-30-SJ
    @Override
    public String removeCustomerById(Long id) {
        Customer customerToRemove = customerRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("Customer","id",id));

        List<Order> ordersToEdit = customerToRemove.getOrder();
        boolean hasActiveOrders = ordersToEdit.stream()
                .anyMatch(Order::getIsActive);

        if (hasActiveOrders) {
            throw new ConflictException("Customer with active orders can't be deleted!");
        }

        MicroMethods.disconnectKeys(
                ordersToEdit,
                order -> order.setCustomer(null),
                order -> orderRepository.save(order)
        );

        /*
        //V1
        for (Order orderToEdit : ordersToEdit) {
            orderToEdit.setCustomer(null);
            orderRepository.save(orderToEdit);
        }



        for (Order orderToEdit : ordersToEdit) {
            if (orderToEdit.getIsActive() == true) {
                throw new ConflictException("Customer with active orders can't be deleted!");
            } else {
                MicroMethods.disconnectKeys(
                        ordersToEdit,
                        order -> order.setCustomer(null),
                        order -> orderRepository.save(order)
                );
            }
        }

         */

        customerRepository.delete(customerToRemove);
        return "Customer " + customerToRemove.getPersonalIdentityNumber() + " has been deleted.";
    }

}
