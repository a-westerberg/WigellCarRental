package com.wigell.wigellcarrental.services;

import com.wigell.wigellcarrental.exceptions.ConflictException;
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

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;


//SA
@SpringBootTest
@Transactional
@Rollback
class CustomerServiceImplAndCustomerRepositoryAndOrderRepositoryIntegrationTest {

    //SA
    private CustomerService customerService;
    //AA added final
    private final CustomerRepository customerRepository;
    //AA added final
    private final OrderRepository orderRepository;

    //SA
    private Customer customerInDB;
    private final Long MISSING_CUSTOMER_ID = 0L;
    private final Principal PRINCIPAL = ()-> "123456-7890";

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
        Customer foundCustomer = customerService.getCustomerById(customerInDB.getId());

        assertEquals(foundCustomer.getFirstName(), customerInDB.getFirstName());
        assertEquals(foundCustomer.getLastName(), customerInDB.getLastName());
        assertEquals(foundCustomer.getEmail(), customerInDB.getEmail());
        assertEquals(foundCustomer.getPhoneNumber(), customerInDB.getPhoneNumber());
        assertEquals(foundCustomer.getAddress(), customerInDB.getAddress());
        assertEquals(foundCustomer.getPersonalIdentityNumber(), customerInDB.getPersonalIdentityNumber());
    }

    //SA
    @Test
    void getCustomerBydShouldResourceNotFoundExceptionWhenCustomerNotFound() {
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                ()-> customerService.getCustomerById(MISSING_CUSTOMER_ID));

        assertThat("Customer not found with id: 0").isEqualTo(exception.getMessage());
    }

    //SA
    @Test
    void updateCustomerShouldReturnUpdatedCustomer() {
        List<Order>customerFromRequestOrders = new ArrayList<>();
        Customer customerFromRequest = new Customer(
                customerInDB.getId(),
                "123456-7890",
                "Kalle",
                "Anka",
                "kalle.anka@gmail.com",
                "9876-1234",
                "12 Street",
                customerFromRequestOrders);

        Customer updatedCustomer = customerService.updateCustomer(customerFromRequest, PRINCIPAL);

        assertEquals(updatedCustomer.getFirstName(), customerFromRequest.getFirstName());
        assertEquals(updatedCustomer.getLastName(), customerFromRequest.getLastName());
        assertEquals(updatedCustomer.getEmail(), customerFromRequest.getEmail());
        assertEquals(updatedCustomer.getPhoneNumber(), customerFromRequest.getPhoneNumber());
        assertEquals(updatedCustomer.getAddress(), customerFromRequest.getAddress());
        assertEquals(updatedCustomer.getPersonalIdentityNumber(), customerFromRequest.getPersonalIdentityNumber());

        Optional<Customer>inDBCustomer = customerRepository.findById(updatedCustomer.getId());
        inDBCustomer.ifPresent(customer -> assertEquals(updatedCustomer.getFirstName(), customer.getFirstName()));

    }

    //SA
    @Test
    void updateCustomerShouldThrowResourceNotFoundExceptionWhenCustomerNotFound() {
        Customer customerFromRequest = new Customer(MISSING_CUSTOMER_ID);
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                ()-> customerService.updateCustomer(customerFromRequest, PRINCIPAL)
        );
        assertThat("Customer not found with id: 0").isEqualTo(exception.getMessage());
    }

    //SA
    @Test
    void updateCustomerShouldThrowConflictExceptionWhenUserIsNotAuthorized() {
        Principal unAuthorizedPrincipal = ()-> "09876-4321";
        ConflictException exception = assertThrows(
                ConflictException.class,
                ()-> customerService.updateCustomer(customerInDB, unAuthorizedPrincipal)
        );
        assertThat("User not authorized for function.").isEqualTo(exception.getMessage());
    }

    //SA
    @Test
    void updateCustomerShouldNotUpdateNullOrIsEmptyValuesOrPersonalIdentityNumber() {
        Customer customerFromRequest = new Customer(customerInDB.getId());
        customerFromRequest.setFirstName("John");
        customerFromRequest.setLastName("");
        customerFromRequest.setAddress(null);
        customerFromRequest.setEmail("john.erikson@gmail.com");
        customerFromRequest.setPersonalIdentityNumber("0976");

        Customer updatedCustomer = customerService.updateCustomer(customerFromRequest, PRINCIPAL);

        assertEquals(updatedCustomer.getFirstName(), customerFromRequest.getFirstName());
        assertEquals(updatedCustomer.getLastName(), customerInDB.getLastName());
        assertEquals(updatedCustomer.getAddress(), customerInDB.getAddress());
        assertEquals(updatedCustomer.getEmail(), customerFromRequest.getEmail());
        assertEquals(updatedCustomer.getPersonalIdentityNumber(), customerInDB.getPersonalIdentityNumber());

        assertNotEquals(updatedCustomer.getEmail(),customerInDB.getEmail());

    }

    //AA
    @Test
    void getAllCustomerShouldReturnListOfAllCustomers() {
        orderRepository.deleteAll();
        customerRepository.deleteAll();

        Customer customer = new Customer(null, "19890101-1234", "Anna", "Andersson", "anna@teste.se", "0701234567", "Stora gatan 1, 123 45, Stockholm", List.of());
        Customer savedCustomer = customerRepository.save(customer);

        List<Customer> customersInDB = customerService.getAllCustomers();
        List<Customer> expectedCustomersInDB = List.of(savedCustomer);

        assertEquals(customersInDB, expectedCustomersInDB);
    }

    //AA
    @Test
    void getAllCustomersShouldThrowResourceNotFoundExceptionWhenListOfCustomersIsEmpty() {
        orderRepository.deleteAll();
        customerRepository.deleteAll();

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class, () -> customerService.getAllCustomers());
        String expectedMessage = "List not found with customers: 0";

        assertEquals(expectedMessage, e.getMessage());
    }

}