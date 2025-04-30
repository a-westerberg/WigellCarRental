package com.wigell.wigellcarrental.services;

import com.wigell.wigellcarrental.models.entities.Customer;
import com.wigell.wigellcarrental.models.entities.Order;
import com.wigell.wigellcarrental.exceptions.ConflictException;
import com.wigell.wigellcarrental.exceptions.InvalidInputException;
import com.wigell.wigellcarrental.exceptions.ResourceNotFoundException;
import com.wigell.wigellcarrental.repositories.CustomerRepository;
import com.wigell.wigellcarrental.repositories.OrderRepository;
import com.wigell.wigellcarrental.services.utilities.MicroMethods;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

//SA
@Service
public class CustomerServiceImpl implements CustomerService{
    //AA
    private final OrderRepository orderRepository;
    //SA
    private CustomerRepository customerRepository;

    //WIG-71-AA
    private static final Logger USER_ANALYZER_LOGGER = LogManager.getLogger("userlog");

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
    public Customer updateCustomer(Customer customer, Principal principal) {

        Customer customerToUpdate = customerRepository.findById(customer.getId()).orElseThrow(() ->
                new ResourceNotFoundException("Customer","id",customer.getId()));

        if (!principal.getName().equals(customerToUpdate.getPersonalIdentityNumber()) && !principal.getName().equals("admin")) {
            System.out.println(principal.getName());
            System.out.println(customer.getPersonalIdentityNumber());
            throw new ConflictException("User not authorized for function.");
        }

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
    public String removeCustomerById(Long id, Principal principal) {
        if (!principal.getName().equals("admin")) {
            throw new ConflictException("User not authorized for function.");
        }

        Customer customerToRemove = customerRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("Customer","id",id));

        List<Order> ordersToEdit = customerToRemove.getOrders();
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

        customerRepository.delete(customerToRemove);

        return "Customer " + customerToRemove.getPersonalIdentityNumber() + " has been deleted.";
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
    // WIG-23-AWS
    @Override
    public Customer addCustomer(Customer customer, Principal principal) {
        validateAddCustomer(customer);
        checkUniquePersonalIdentityNumber(customer.getPersonalIdentityNumber());

        Customer savedCustomer = customerRepository.save(customer);
        USER_ANALYZER_LOGGER.info("User '{}' added a new customer with personalIdentityNumber: {}", principal.getName(), savedCustomer.getPersonalIdentityNumber());
        return savedCustomer;
    }

    private void validateAddCustomer(Customer customer) {
        MicroMethods.validateData("Customer", "personalIdentityNumber", customer.getPersonalIdentityNumber());
        MicroMethods.validateData("Customer", "firstName", customer.getFirstName());
        MicroMethods.validateData("Customer", "lastName", customer.getLastName());
        MicroMethods.validateData("Customer", "email", customer.getEmail());
        MicroMethods.validateData("Customer", "phoneNumber", customer.getPhoneNumber());
        MicroMethods.validateData("Customer", "address", customer.getAddress());

        if (!customer.getPersonalIdentityNumber().matches("\\d{8}-\\d{4}")){
            throw new InvalidInputException("Customer", "personalIdentityNumber",customer.getPersonalIdentityNumber());
        }
    }

    private void checkUniquePersonalIdentityNumber(String personalIdentityNumber) {
        MicroMethods.validateUniqueValue(
                "personalIdentityNumber",
                personalIdentityNumber,
                value -> customerRepository.findByPersonalIdentityNumber(value).isPresent()
        );
    }

}
