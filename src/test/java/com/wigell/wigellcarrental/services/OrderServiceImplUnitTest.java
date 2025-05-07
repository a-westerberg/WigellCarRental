package com.wigell.wigellcarrental.services;

import com.wigell.wigellcarrental.enums.CarStatus;
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

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
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
    private Car testCar;
    private Customer testCustomer;
    private Principal testPrincipal;
    private Order testOrder;

    //AA
    @BeforeEach
    public void setUp(){
        orderService = new OrderServiceImpl(mockOrderRepository, mockCarRepository, mockCustomerRepository);

        testCar = new Car(10L, "Volvo", "V90","ABC789", CarStatus.AVAILABLE, BigDecimal.valueOf(1000), List.of());
        testCustomer = new Customer(1L, "19890101-1234", "Anna", "Andersson", "anna@test.se", "070-1234567", "Solrosvägen 1, 90347 Umeå", List.of());
        testOrder = new Order(1L, LocalDate.of(2025,1,1),LocalDate.now(),LocalDate.now().plusDays(5), testCar, testCustomer, BigDecimal.valueOf(5000),true, false);
        testPrincipal = () -> testCustomer.getPersonalIdentityNumber();
    }


    //AA
    @Test
    void cancelOrderShouldSetIsActiveToFalse() {
        when(mockOrderRepository.findById(testOrder.getId())).thenReturn(Optional.of(testOrder));
        when(mockOrderRepository.save(any(Order.class))).thenReturn(testOrder);

        orderService.cancelOrder(1L, testPrincipal);

        assertFalse(testOrder.getIsActive());
    }

    //AA
    @Test
    void cancelOrderShouldSetCorrectCancellationFee(){
        when(mockOrderRepository.findById(testOrder.getId())).thenReturn(Optional.of(testOrder));
        when(mockOrderRepository.save(any(Order.class))).thenReturn(testOrder);

        orderService.cancelOrder(1L, testPrincipal);
        long orderDaysLong = 5L;

        BigDecimal originalPrice = testOrder.getTotalPrice();
        orderService.cancelOrder(1L, testPrincipal);

        BigDecimal expectedFee = originalPrice
                .multiply(BigDecimal.valueOf(0.05))
                .multiply(BigDecimal.valueOf(orderDaysLong));

        assertEquals(0, expectedFee.compareTo(testOrder.getTotalPrice()));
    }

    //AA
    @Test
    void cancelOrderShouldReturnStringMessageOrderWithIdIsCancelled(){
        when(mockOrderRepository.findById(testOrder.getId())).thenReturn(Optional.of(testOrder));
        when(mockOrderRepository.save(any(Order.class))).thenReturn(testOrder);

        String returnMessage = orderService.cancelOrder(1L, testPrincipal);

        assertEquals("Order with id '1' is cancelled", returnMessage);
    }

    //AA
    @Test
    void cancelOrderShouldThrowResourceNotFoundExceptionIfOrderIdDoseNotExist(){
        Long missingOrderId = -1L;
        when(mockOrderRepository.findById(missingOrderId)).thenReturn(Optional.empty());

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class, () -> orderService.cancelOrder(missingOrderId, testPrincipal));

        assertEquals("Order not found with id: " + missingOrderId, e.getMessage());
    }

    //AA
    @Test
    void cancelOrderShouldReturnMessageIfNoOrderWithThatIdExist(){
        Long orderId = 1L;

        Customer otherCustomer = new Customer(
                2L, "20000101-9999", "Ove", "Olsson", "ove@fake.se", "070-1111111", "Falskgatan 1", List.of()
        );


        Order foreignOrder = new Order(
                orderId,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3),
                LocalDate.now(),
                testCar,
                otherCustomer,
                BigDecimal.valueOf(2000),
                true,
                false
        );

        when(mockOrderRepository.findById(orderId)).thenReturn(Optional.of(foreignOrder));

        String returnMessage = orderService.cancelOrder(orderId, testPrincipal);

        assertEquals("No order for '19890101-1234' with id: 1", returnMessage);
    }

    //AA
    @Test
    void cancelOrderShouldReturnMessageIfOrderAlreadyStarted(){
        when(mockOrderRepository.findById(testOrder.getId())).thenReturn(Optional.of(testOrder));

        testOrder.setStartDate(LocalDate.now().minusDays(1));
        testOrder.setEndDate(LocalDate.now().plusDays(1));

        String returnMessage = orderService.cancelOrder(testOrder.getId(), testPrincipal);

        assertEquals("Order has already started and can't then be cancelled", returnMessage);

    }

    //AA
    @Test
    void cancelOrderShouldReturnMessageIfOrderAlreadyEnded(){
        when(mockOrderRepository.findById(testOrder.getId())).thenReturn(Optional.of(testOrder));

        testOrder.setStartDate(LocalDate.now().minusDays(10));
        testOrder.setEndDate(LocalDate.now().minusDays(8));

        String returnMessage = orderService.cancelOrder(testOrder.getId(), testPrincipal);

        assertEquals("Order has already ended", returnMessage);

    }


}