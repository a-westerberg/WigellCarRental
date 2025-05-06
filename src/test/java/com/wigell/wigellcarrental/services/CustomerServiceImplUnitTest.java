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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

//SA
@ExtendWith(MockitoExtension.class)
class CustomerServiceImplUnitTest {

    //@InjectMocks //krachar med den
    private CustomerService customerService;

    @Mock
    private CustomerRepository mockCustomerRepository;
    @Mock
    private OrderRepository mockOrderRepository;

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
        //Given
        when(mockCustomerRepository.findById(customerInDB.getId())).thenReturn(Optional.of(customerInDB));

        //When
        Customer customerFound = customerService.getCustomerById(customerInDB.getId());

        //Then
        assertEquals(customerInDB.getId(), customerFound.getId());
        assertEquals(customerInDB.getFirstName(), customerFound.getFirstName());
        assertEquals(customerInDB.getLastName(), customerFound.getLastName());
        assertEquals(customerInDB.getPersonalIdentityNumber(), customerFound.getPersonalIdentityNumber());

    }

    //SA
    @Test
    void getCustomerByIdThrowsResourceNotFoundExceptionWhenCustomerNotFound() {
        //Given
        when(mockCustomerRepository.findById(MISSING_CUSTOMER_ID)).thenReturn(Optional.empty());

        //When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> customerService.getCustomerById(MISSING_CUSTOMER_ID));

        //Then
        assertEquals(exception.getMessage(), "Customer not found with id: 0");

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


    //SA //Kanske ta bort senare om principal admin tas bort
    @Test
    void updateCustomerShouldReturnUpdatedCustomerIfPrincipalIsAdmin() {
        //Given
        Customer customerFromRequest = new Customer(customerInDB.getId());
        customerFromRequest.setAddress("123 Street");

        Principal principalAdmin = () -> "admin";

        when(mockCustomerRepository.findById(customerFromRequest.getId())).thenReturn(Optional.of(customerInDB));
        when(mockCustomerRepository.getCustomersById(customerFromRequest.getId())).thenReturn(customerInDB);

        //When
        Customer updatedCustomer = customerService.updateCustomer(customerFromRequest, principalAdmin);

        //Then
        verify(mockCustomerRepository).save(any(Customer.class));
        assertThat(updatedCustomer.getAddress()).isEqualTo("123 Street");
        assertThat(updatedCustomer.getFirstName()).isEqualTo("John");
    }

    //SA
    @Test
    void updateCustomerThrowsResourceNotFoundExceptionWhenCustomerNotFound() {
        //Given
        Customer missingCustomer = new Customer(MISSING_CUSTOMER_ID);
        missingCustomer.setPersonalIdentityNumber("123456-7890");

        when(mockCustomerRepository.findById(MISSING_CUSTOMER_ID)).thenReturn(Optional.empty());

        //When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> customerService.updateCustomer(missingCustomer, PRINCIPAL)
        );

        //Then
        assertEquals(exception.getMessage(), "Customer not found with id: 0");
    }

    //SA
    @Test
    void updateCustomerShouldThrowConflictExceptionWhenPrincipalIsNotEqualToCustomerPersonalIdentityNumber() {
        //Given
        when(mockCustomerRepository.findById(customerInDB.getId())).thenReturn(Optional.of(customerInDB));
        Principal wrongPrincipal = () -> "098765-4321";

        //When & Then
        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> customerService.updateCustomer(customerInDB, wrongPrincipal)
        );

        assertEquals(exception.getMessage(), "User not authorized for function.");
    }

    //SA
    @Test
    void updateCustomerShouldNotOverwriteFieldsWithNullOrEmptyOrPersonalIdentityNumber() {
        //Given
        Customer customerFromRequest = new Customer(customerInDB.getId());
        customerFromRequest.setFirstName("");
        customerFromRequest.setAddress(null);
        customerFromRequest.setPersonalIdentityNumber("6778");
        customerFromRequest.setPhoneNumber("3941");


        when(mockCustomerRepository.findById(customerFromRequest.getId())).thenReturn(Optional.of(customerInDB));
        when(mockCustomerRepository.getCustomersById(customerFromRequest.getId())).thenReturn(customerInDB);

        //When
        Customer updatedCustomer = customerService.updateCustomer(customerFromRequest, PRINCIPAL);

        //Then
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
}
