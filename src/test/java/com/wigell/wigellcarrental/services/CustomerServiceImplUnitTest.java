package com.wigell.wigellcarrental.services;

import com.wigell.wigellcarrental.exceptions.ConflictException;
import com.wigell.wigellcarrental.exceptions.ResourceNotFoundException;
import com.wigell.wigellcarrental.models.entities.Customer;
import com.wigell.wigellcarrental.repositories.CustomerRepository;
import com.wigell.wigellcarrental.repositories.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    private Customer customer = new Customer();

    //SA
    @BeforeEach
    void setUp() {
        customerService = new CustomerServiceImpl(mockCustomerRepository, mockOrderRepository);

        customer.setId(1L);
        customer.setFirstName("John");
        customer.setLastName("Smith");
        customer.setEmail("john.smith@gmail.com");
        customer.setPhoneNumber("0987654321");
        customer.setAddress("1 Road");
        customer.setPersonalIdentityNumber("123456-7890");
    }

    //SA
    @Test
    void getCustomerByIdReturnsCustomer() {
        //Given
        when(mockCustomerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));

        //When
        Customer customerFound = customerService.getCustomerById(customer.getId());

        //Then
        assertEquals(customer.getId(), customerFound.getId());
        assertEquals(customer.getFirstName(), customerFound.getFirstName());
        assertEquals(customer.getLastName(), customerFound.getLastName());
        assertEquals(customer.getPersonalIdentityNumber(), customerFound.getPersonalIdentityNumber());

    }

    //SA
    @Test
    void getCustomerByIdThrowsResourceNotFoundExceptionWhenCustomerNotFound() {
        //Given
        Long missingId = 2L;
        when(mockCustomerRepository.findById(missingId)).thenReturn(Optional.empty());

        //When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                ()-> customerService.getCustomerById(missingId));

        //Then
        assertEquals(exception.getMessage(), "Customer not found with id: "+missingId);

    }

    //SA
    @Test
    void updateCustomerShouldReturnUpdatedCustomer() {
        //Given
        customer.setAddress("123 Street");
        when(mockCustomerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        Principal principal = () -> "123456-7890";

        //When
        customerService.updateCustomer(customer, principal);

        //Then
        verify(mockCustomerRepository).save(customer);
        assertThat(customer.getAddress()).isEqualTo("123 Street");
    }

    //SA
    @Test
    void updateCustomerThrowsResourceNotFoundExceptionWhenCustomerNotFound() {
        //Given
        Long missingId = 2L;
        customer.setId(missingId);
        Principal principal = () -> "123456-7890";
        when(mockCustomerRepository.findById(missingId)).thenReturn(Optional.empty());

        //When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                ()-> customerService.updateCustomer(customer, principal)
        );

        //Then
        assertEquals(exception.getMessage(), "Customer not found with id: "+missingId);
    }

    //SA
    @Test
    void updateCustomerShouldThrowConflictExceptionWhenPrincipalIsNotEqualToCustomerPersonalIdentityNumber() {
        //Given
        when(mockCustomerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        Principal principal = () -> "098765-4321";

        //When & Then
        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> customerService.updateCustomer(customer, principal)
        );

        assertEquals(exception.getMessage(), "User not authorized for function.");
    }

    //TODO: check with Simon on validateCustomer
    //SA
    @Test
    void updateCustomerShouldNotOverwriteFieldsWithNullOrEmpty(){
        //Given
        Customer updatedCustomer = new Customer();
        updatedCustomer.setId(customer.getId());
        updatedCustomer.setFirstName("");
        updatedCustomer.setAddress(null);
        updatedCustomer.setPhoneNumber("3941");

        when(mockCustomerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        Principal principal = () -> "123456-7890";

        //When
        customerService.updateCustomer(updatedCustomer, principal);

        //Then
        verify(mockCustomerRepository).save(customer);
        assertEquals(updatedCustomer.getId(), customer.getId());
        assertEquals("John", customer.getFirstName());
        assertEquals("1 Road", customer.getAddress());
        assertEquals(updatedCustomer.getPhoneNumber(), customer.getPhoneNumber());
    }
}