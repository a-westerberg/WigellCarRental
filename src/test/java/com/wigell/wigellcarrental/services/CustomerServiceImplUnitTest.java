package com.wigell.wigellcarrental.services;

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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        customer.setPersonalIdentityNumber("123456-7890");
    }

    //SA
    @Test
    void getCustomerByIdReturnsCustomer() {
        when(mockCustomerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));

        Customer customerFound = customerService.getCustomerById(customer.getId());

        assertEquals(customer.getId(), customerFound.getId());
        assertEquals(customer.getFirstName(), customerFound.getFirstName());
        assertEquals(customer.getLastName(), customerFound.getLastName());
        assertEquals(customer.getPersonalIdentityNumber(), customerFound.getPersonalIdentityNumber());

    }

    //SA
    @Test
    void getCustomerByIdThrowsResourceNotFoundExceptionWhenCustomerNotFound() {
        Long missingId = 2L;
        when(mockCustomerRepository.findById(missingId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                ()-> customerService.getCustomerById(missingId));

        assertEquals(exception.getMessage(), "Customer not found with id: "+missingId);

    }
}