package com.wigell.wigellcarrental.services;

import com.wigell.wigellcarrental.exceptions.ResourceNotFoundException;
import com.wigell.wigellcarrental.models.entities.Customer;
import com.wigell.wigellcarrental.models.entities.Order;
import com.wigell.wigellcarrental.repositories.CustomerRepository;
import com.wigell.wigellcarrental.repositories.OrderRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

//SA
@SpringBootTest
@Transactional
@Rollback
class CustomerServiceImplAndCustomerRepositoryAndOrderRepositoryIntegrationTest {

    private CustomerService customerService;
    private CustomerRepository customerRepository;
    private OrderRepository orderRepository;

    private Customer customerInDB;
    private final Long MISSING_CUSTOMER_ID = 0L;

    //SA
    @Autowired
    public CustomerServiceImplAndCustomerRepositoryAndOrderRepositoryIntegrationTest(CustomerService customerService, CustomerRepository customerRepository, OrderRepository orderRepository) {
        this.customerService = customerService;
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
    }

    //SA
    @BeforeEach
    void setUp() {
        customerService = new CustomerServiceImpl(customerRepository, orderRepository);

        List<Order> customerInDBOrders = new ArrayList<>();
        customerInDB = new Customer(1L,
                "123456-7890",
                "John",
                "Smith",
                "john.smith@gmail.com",
                "0987654321",
                "1 Road",
                customerInDBOrders);

        customerRepository.save(customerInDB);
    }

    //SA
    @Test
    void getCustomerByIdShouldReturnCustomer() {
        Customer FoundCustomer = customerService.getCustomerById(customerInDB.getId());

        assertEquals(FoundCustomer.getFirstName(), customerInDB.getFirstName());
        assertEquals(FoundCustomer.getLastName(), customerInDB.getLastName());
        assertEquals(FoundCustomer.getEmail(), customerInDB.getEmail());
        assertEquals(FoundCustomer.getPhoneNumber(), customerInDB.getPhoneNumber());
        assertEquals(FoundCustomer.getAddress(), customerInDB.getAddress());
        assertEquals(FoundCustomer.getPersonalIdentityNumber(), customerInDB.getPersonalIdentityNumber());
    }

    //SA
    @Test
    void getCustomerBydShouldResourceNotFoundExceptionWhenCustomerNotFound() {
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                ()-> customerService.getCustomerById(MISSING_CUSTOMER_ID));

        assertThat(exception.getMessage()).isEqualTo("Customer not found with id: 0");
    }

}