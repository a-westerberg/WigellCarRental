package com.wigell.wigellcarrental.services;

import com.wigell.wigellcarrental.exceptions.ResourceNotFoundException;
import com.wigell.wigellcarrental.models.entities.Car;
import com.wigell.wigellcarrental.models.entities.Customer;
import com.wigell.wigellcarrental.models.entities.Order;
import com.wigell.wigellcarrental.repositories.CarRepository;
import com.wigell.wigellcarrental.repositories.CustomerRepository;
import com.wigell.wigellcarrental.repositories.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Mockito.when;

//WIG-48-AA
@ExtendWith(MockitoExtension.class)
class OrderServiceImplUnitTest {

    //AA
    @Mock
    private OrderRepository mockOrderRepository;

    //AA
    @Mock
    private CarRepository mockCarRepository;

    //AA
    @Mock
    CustomerRepository mockCustomerRepository;

    //AA
    private OrderServiceImpl orderService;


    //AA
    @Mock
    Car mockCar;

    //AA
    @Mock
    Customer mockCustomer;

    //AA
    @Mock
    Principal mockPrincipal = () -> mockCustomer.getPersonalIdentityNumber();

    //AA
    private Order testOrder = new Order();

    //AA
    @BeforeEach
    public void setUp(){
        orderService = new OrderServiceImpl(mockOrderRepository, mockCarRepository, mockCustomerRepository);

        testOrder.setId(1L);
        testOrder.setCar();
        testOrder.setCustomer();
        testOrder.setBookedAt(LocalDate.of(2025,1,1));
        testOrder.setIsActive(true);
        testOrder.setStartDate(LocalDate.of(2025,4,1));
        testOrder.setEndDate(LocalDate.of(2025,4,5));
        testOrder.setTotalPrice();
    }


    //AA
    @Test
    void cancelOrderShouldSetIsActiveToFalse() {
        when(mockOrderRepository.findById(testOrder.getId())).thenReturn(Optional.of(testOrder));
        when(mockOrderRepository.save(any(Order.class))).thenReturn(testOrder);

        orderService.cancelOrder(1L, mockPrincipal);

        assertFalse(testOrder.getIsActive());
    }

    //AA
    @Test
    void cancelOrderShouldSetCorrectCancellationFee(){

    }

    //AA
    @Test
    void cancelOrderShouldReturnStringMessageOrderWithIdIsCancelled(){
        when(mockOrderRepository.findById(testOrder.getId())).thenReturn(Optional.of(testOrder));
        when(mockOrderRepository.save(any(Order.class))).thenReturn(testOrder);

        String returnMessage = orderService.cancelOrder(1L, mockPrincipal);

        assertEquals("Order with id '1' is cancelled", returnMessage);
    }

    //AA
    @Test
    void cancelOrderShouldThrowResourceNotFoundExceptionIfOrderIdDoseNotExist(){
        Long missingOrderId = -1L;
        when(mockOrderRepository.findById(missingOrderId)).thenReturn(Optional.empty());

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class, () -> orderService.cancelOrder(missingOrderId, mockPrincipal));

        assertEquals("Order not found with id: " + missingOrderId, e.getMessage());
    }

    //AA
    @Test
    void cancelOrderShouldReturnMessageIfNoOrderWithThatIdExist(){

    }

    //AA
    @Test
    void cancelOrderShouldReturnMessageIfOrderAlreadyStarted(){

    }

    //AA
    @Test
    void cancelOrderShouldReturnMessageIfOrderAlreadyEnded(){

    }

    //Egna tester för loggningen??


}