package com.wigell.wigellcarrental.services;

import com.wigell.wigellcarrental.exceptions.ConflictException;
import com.wigell.wigellcarrental.exceptions.ResourceNotFoundException;
import com.wigell.wigellcarrental.models.entities.Customer;
import com.wigell.wigellcarrental.models.entities.Order;
import com.wigell.wigellcarrental.repositories.CustomerRepository;
import com.wigell.wigellcarrental.repositories.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

//SA
@ExtendWith(MockitoExtension.class)
class CustomerServiceImplUnitTest {

    //SA
    private CustomerService customerService;
    @Mock
    private CustomerRepository mockCustomerRepository;
    @Mock
    private OrderRepository mockOrderRepository;

    //SA
    private Customer customerInDB;
    private final Long MISSING_CUSTOMER_ID = 0L;
    private final Principal PRINCIPAL = () -> "123456-7890";

    //SA
    @BeforeEach
    void setUp() {
        customerService = new CustomerServiceImpl(mockCustomerRepository, mockOrderRepository);

        List<Order> customerInDBOrders = new ArrayList<>();
        customerInDB = new Customer(1L,
                "123456-7890",
                "John",
                "Smith",
                "john.smith@gmail.com",
                "0987654321",
                "1 Road",
                customerInDBOrders);
    }

    //SA
    @Test
    void getCustomerByIdReturnsCustomer() {
        when(mockCustomerRepository.findById(customerInDB.getId())).thenReturn(Optional.of(customerInDB));

        Customer customerFound = customerService.getCustomerById(customerInDB.getId());

        assertEquals(customerInDB.getId(), customerFound.getId());
        assertEquals(customerInDB.getFirstName(), customerFound.getFirstName());
        assertEquals(customerInDB.getLastName(), customerFound.getLastName());
        assertEquals(customerInDB.getPersonalIdentityNumber(), customerFound.getPersonalIdentityNumber());

    }

    //SA
    @Test
    void getCustomerByIdThrowsResourceNotFoundExceptionWhenCustomerNotFound() {
        when(mockCustomerRepository.findById(MISSING_CUSTOMER_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> customerService.getCustomerById(MISSING_CUSTOMER_ID));

        assertEquals("Customer not found with id: 0",exception.getMessage());

    }

    //SA
    @Test
    void updateCustomerOnAllFieldsAndCorrectPrincipalShouldReturnUpdatedCustomer() {
        List<Order>customerFromRequestOrders = new ArrayList<>();
        Customer customerFromRequest = new Customer(
                customerInDB.getId(),
                "123456-7890",
                "Kalle",
                "Anka",
                "kalle.andka@gmail.com",
                "3941",
                "123 Street",
                customerFromRequestOrders
        );

        when(mockCustomerRepository.findById(customerFromRequest.getId())).thenReturn(Optional.of(customerInDB));
        when(mockCustomerRepository.getCustomersById(customerFromRequest.getId())).thenReturn(customerInDB);

        Customer updatedCustomer = customerService.updateCustomer(customerFromRequest, PRINCIPAL);

        verify(mockCustomerRepository).save(customerInDB);
        assertEquals(updatedCustomer.getId(), customerInDB.getId());
        assertEquals("Kalle", updatedCustomer.getFirstName());
        assertEquals("Anka", updatedCustomer.getLastName());
        assertEquals("123 Street", updatedCustomer.getAddress());
        assertEquals("3941", updatedCustomer.getPhoneNumber());
        assertEquals("kalle.andka@gmail.com", updatedCustomer.getEmail());

    }

    //SA
    @Test
    void updateCustomerThrowsResourceNotFoundExceptionWhenCustomerNotFound() {
        Customer missingCustomer = new Customer(MISSING_CUSTOMER_ID);
        missingCustomer.setPersonalIdentityNumber("123456-7890");

        when(mockCustomerRepository.findById(MISSING_CUSTOMER_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> customerService.updateCustomer(missingCustomer, PRINCIPAL)
        );

        assertEquals("Customer not found with id: 0",exception.getMessage());
    }

    //SA
    @Test
    void updateCustomerShouldThrowConflictExceptionWhenPrincipalIsNotEqualToCustomerPersonalIdentityNumber() {
        when(mockCustomerRepository.findById(customerInDB.getId())).thenReturn(Optional.of(customerInDB));
        Principal wrongPrincipal = () -> "098765-4321";

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> customerService.updateCustomer(customerInDB, wrongPrincipal)
        );

        assertEquals("User not authorized for function.", exception.getMessage());
    }

    //SA
    @Test
    void updateCustomerShouldNotOverwriteFieldsWithNullOrEmptyOrPersonalIdentityNumber() {
        Customer customerFromRequest = new Customer(customerInDB.getId());
        customerFromRequest.setFirstName("");
        customerFromRequest.setAddress(null);
        customerFromRequest.setPersonalIdentityNumber("6778");
        customerFromRequest.setPhoneNumber("3941");


        when(mockCustomerRepository.findById(customerFromRequest.getId())).thenReturn(Optional.of(customerInDB));
        when(mockCustomerRepository.getCustomersById(customerFromRequest.getId())).thenReturn(customerInDB);

        Customer updatedCustomer = customerService.updateCustomer(customerFromRequest, PRINCIPAL);

        verify(mockCustomerRepository).save(customerInDB);
        assertEquals(updatedCustomer.getId(), customerInDB.getId());
        assertEquals(updatedCustomer.getPersonalIdentityNumber(), customerInDB.getPersonalIdentityNumber());
        assertEquals(updatedCustomer.getFirstName(),customerInDB.getFirstName());
        assertEquals(updatedCustomer.getAddress(),customerInDB.getAddress());
        assertEquals(updatedCustomer.getPhoneNumber(),customerFromRequest.getPhoneNumber());

        assertNotEquals(updatedCustomer.getPersonalIdentityNumber(), customerFromRequest.getPersonalIdentityNumber());
        assertNotEquals(updatedCustomer.getFirstName(), customerFromRequest.getFirstName());
        assertNotEquals(updatedCustomer.getAddress(), customerFromRequest.getAddress());


    }

    //WIG-49-AA
    @Test
    void getAllCustomerShouldReturnListOfAllCustomers() {
        when(mockCustomerRepository.findAll()).thenReturn(List.of(customerInDB));

        List<Customer> customersInDB = customerService.getAllCustomers();
        List<Customer> expectedCustomersInDB = List.of(customerInDB);

        assertEquals(customersInDB, expectedCustomersInDB);
    }

    //WIG-49-AA
    @Test
    void getAllCustomersShouldThrowResourceNotFoundExceptionWhenListOfCustomersIsEmpty() {
        when(mockCustomerRepository.findAll()).thenReturn(List.of());

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class, () -> customerService.getAllCustomers());
        String expectedMessage = "List not found with customers: 0";

        assertEquals(expectedMessage, e.getMessage());
    }

    // WIG-103-AWS
    @Test
    void getOrdersShouldReturnOrdersForCustomer(){
        Principal principal = () -> "123456-7890";
        Customer customer = new Customer();
        customer.setPersonalIdentityNumber("123456-7890");

        Order mockOrder = new Order();
        List<Order> expectedOrders = List.of(mockOrder);

        when(mockCustomerRepository.findByPersonalIdentityNumber("123456-7890"))
                .thenReturn(Optional.of(customer));

        when(mockOrderRepository.findByCustomer_PersonalIdentityNumberAndEndDateBefore(
                eq("123456-7890"), any(LocalDate.class)))
                .thenReturn(expectedOrders);

        List<Order> actualOrders = customerService.getOrders(principal);

        assertEquals(expectedOrders, actualOrders);
    }

    // WIG-103-AWS
    @Test
    void getOrderShouldThrowResourceNotFoundExceptionWhenCustomerNotFound(){
        Principal principal = () -> "000000-0000";

        when(mockCustomerRepository.findByPersonalIdentityNumber("000000-0000"))
                .thenReturn(Optional.empty());

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class, () -> customerService.getOrders(principal));

        assertEquals("Customer not found with user: 000000-0000", e.getMessage());
    }

}
