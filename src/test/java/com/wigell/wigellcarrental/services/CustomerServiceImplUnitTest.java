package com.wigell.wigellcarrental.services;

import com.wigell.wigellcarrental.exceptions.ConflictException;
import com.wigell.wigellcarrental.exceptions.ResourceNotFoundException;
import com.wigell.wigellcarrental.models.entities.Customer;
import com.wigell.wigellcarrental.repositories.CustomerRepository;
import com.wigell.wigellcarrental.repositories.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    //SA
    @BeforeEach
    void setUp() {
        customerService = new CustomerServiceImpl(mockCustomerRepository, mockOrderRepository);

        customerInDB = new Customer();
        customerInDB.setId(1L);
        customerInDB.setFirstName("John");
        customerInDB.setLastName("Smith");
        customerInDB.setEmail("john.smith@gmail.com");
        customerInDB.setPhoneNumber("0987654321");
        customerInDB.setAddress("1 Road");
        customerInDB.setPersonalIdentityNumber("123456-7890");
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
        Customer customerFromRequest = new Customer();
        customerFromRequest.setId(1L);
        customerFromRequest.setAddress("123 Street");

        Principal principal = () -> "123456-7890";

        when(mockCustomerRepository.findById(customerInDB.getId())).thenReturn(Optional.of(customerInDB));
        when(mockCustomerRepository.save(any(Customer.class))).thenAnswer(i -> i.getArguments()[0]);


        //When
        Customer updatedCustomer = customerService.updateCustomer(customerFromRequest, principal);

        //Then
        verify(mockCustomerRepository).save(any(Customer.class));
        assertThat(updatedCustomer.getAddress()).isEqualTo("123 Street");
        assertThat(updatedCustomer.getFirstName()).isEqualTo("John");
    }

    //SA
    @Test
    void updateCustomerThrowsResourceNotFoundExceptionWhenCustomerNotFound() {
        //Given
        Long missingId = 2L;
        Customer missingCustomer = new Customer();
        missingCustomer.setId(missingId);
        missingCustomer.setPersonalIdentityNumber("123456-7890");
        Principal principal = () -> "123456-7890";

        when(mockCustomerRepository.findById(missingId)).thenReturn(Optional.empty());

        //When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                ()-> customerService.updateCustomer(missingCustomer, principal)
        );

        //Then
        assertEquals(exception.getMessage(), "Customer not found with id: "+missingId);
    }

    //SA
    @Test
    void updateCustomerShouldThrowConflictExceptionWhenPrincipalIsNotEqualToCustomerPersonalIdentityNumber() {
        //Given
        when(mockCustomerRepository.findById(customerInDB.getId())).thenReturn(Optional.of(customerInDB));
        Principal principal = () -> "098765-4321";

        //When & Then
        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> customerService.updateCustomer(customerInDB, principal)
        );

        assertEquals(exception.getMessage(), "User not authorized for function.");
    }

    //SA
    @Test
    void updateCustomerShouldNotOverwriteFieldsWithNullOrEmpty(){
        //Given
        Customer customerFromRequest = new Customer();
        customerFromRequest.setId(1L);
        customerFromRequest.setFirstName("");
        customerFromRequest.setAddress(null);
        customerFromRequest.setPhoneNumber("3941");

        Principal principal = () -> "123456-7890";

        when(mockCustomerRepository.findById(customerInDB.getId())).thenReturn(Optional.of(customerInDB));
        when(mockCustomerRepository.save(any(Customer.class))).thenAnswer(i -> i.getArguments()[0]);

        //When
        Customer updatedCustomer = customerService.updateCustomer(customerFromRequest, principal);

        //Then
        verify(mockCustomerRepository).save(customerInDB);
        assertEquals(updatedCustomer.getId(), customerInDB.getId());
        assertEquals("John", updatedCustomer.getFirstName());
        assertEquals("1 Road", updatedCustomer.getAddress());
        assertEquals("3941", updatedCustomer.getPhoneNumber());
    }

    //SA
    @Test
    void updateCustomerShouldNotUpdatePersonalIdentityNumber(){
        Customer customerFromRequest = new Customer();
        customerFromRequest.setId(1L);
        customerFromRequest.setPersonalIdentityNumber("6778");
        customerFromRequest.setAddress("123 Street");

        Principal principal = () -> "123456-7890";

        when(mockCustomerRepository.findById(customerInDB.getId())).thenReturn(Optional.of(customerInDB));
        when(mockCustomerRepository.save(any(Customer.class))).thenAnswer(i -> i.getArguments()[0]);

        //When
        Customer updatedCustomer = customerService.updateCustomer(customerFromRequest, principal);

        //Then
        verify(mockCustomerRepository).save(customerInDB);
        assertEquals(updatedCustomer.getId(), customerInDB.getId());
        assertEquals("John", updatedCustomer.getFirstName());
        assertEquals("123 Street", updatedCustomer.getAddress());
        assertEquals("123456-7890", updatedCustomer.getPersonalIdentityNumber());
        assertThat(updatedCustomer.getPersonalIdentityNumber()).isNotEqualTo("6778");
    }
}