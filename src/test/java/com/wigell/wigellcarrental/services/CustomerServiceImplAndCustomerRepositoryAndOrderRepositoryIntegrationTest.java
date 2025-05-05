package com.wigell.wigellcarrental.services;

import com.wigell.wigellcarrental.exceptions.ResourceNotFoundException;
import com.wigell.wigellcarrental.models.entities.Customer;
import com.wigell.wigellcarrental.repositories.CustomerRepository;
import com.wigell.wigellcarrental.repositories.OrderRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

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

        customerInDB = new Customer();
        customerInDB.setFirstName("John");
        customerInDB.setLastName("Smith");
        customerInDB.setEmail("john.smith@gmail.com");
        customerInDB.setPhoneNumber("1234567890");
        customerInDB.setAddress("123 Main Street");
        customerInDB.setPersonalIdentityNumber("123456-7890");
        customerRepository.save(customerInDB);
    }

    //SA
    @Test
    void getCustomerByIdShouldReturnCustomer() {
        Customer customer = customerService.getCustomerById(customerInDB.getId());

        assertEquals(customer.getFirstName(), customerInDB.getFirstName());
        assertEquals(customer.getLastName(), customerInDB.getLastName());
        assertEquals(customer.getEmail(), customerInDB.getEmail());
        assertEquals(customer.getPhoneNumber(), customerInDB.getPhoneNumber());
        assertEquals(customer.getAddress(), customerInDB.getAddress());
        assertEquals(customer.getPersonalIdentityNumber(), customerInDB.getPersonalIdentityNumber());
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