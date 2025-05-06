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
    private final Long MISSING_CUSTOMER_ID = 0L;

    //SA
    @BeforeEach
    void setUp() {
        customerService = new CustomerServiceImpl(mockCustomerRepository, mockOrderRepository);

        List<Order>customerInDBOrders = new ArrayList<>();
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
                ()-> customerService.getCustomerById(MISSING_CUSTOMER_ID));

        //Then
        assertEquals(exception.getMessage(), "Customer not found with id: 0");

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

    //SA //Kanske ta bort senare om principal admin tas bort
    @Test
    void updateCustomerShouldReturnUpdatedCustomerWithPrincipalAdmin() {
        //Given
        Customer customerFromRequest = new Customer();
        customerFromRequest.setId(1L);
        customerFromRequest.setAddress("123 Street");

        Principal principal = () -> "admin";

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
        Customer missingCustomer = new Customer();
        missingCustomer.setId(MISSING_CUSTOMER_ID);
        missingCustomer.setPersonalIdentityNumber("123456-7890");
        Principal principal = () -> "123456-7890";

        when(mockCustomerRepository.findById(MISSING_CUSTOMER_ID)).thenReturn(Optional.empty());

        //When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                ()-> customerService.updateCustomer(missingCustomer, principal)
        );

        //Then
        assertEquals(exception.getMessage(), "Customer not found with id: 0");
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

    //SA
    @Test
    void updateCustomerInAllFieldsShouldBeUpdated() {
        List<Order>customerFromRequestOrders = new ArrayList<>();
        Customer customerFromRequest = new Customer(
                1L,
                "123456-7890",
                "Kalle",
                "Anka",
                "kalle.andka@gmail.com",
                "3941",
                "123 Street",
                customerFromRequestOrders
        );

        Principal principal = () -> "123456-7890";
        when(mockCustomerRepository.findById(customerInDB.getId())).thenReturn(Optional.of(customerInDB));
        when(mockCustomerRepository.save(any(Customer.class))).thenAnswer(i -> i.getArguments()[0]);

        Customer updatedCustomer = customerService.updateCustomer(customerFromRequest, principal);

        verify(mockCustomerRepository).save(customerInDB);
        assertEquals(updatedCustomer.getId(), customerInDB.getId());
        assertEquals("Kalle", updatedCustomer.getFirstName());
        assertEquals("Anka", updatedCustomer.getLastName());
        assertEquals("123 Street", updatedCustomer.getAddress());
        assertEquals("3941", updatedCustomer.getPhoneNumber());
        assertEquals("kalle.andka@gmail.com", updatedCustomer.getEmail());

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
}