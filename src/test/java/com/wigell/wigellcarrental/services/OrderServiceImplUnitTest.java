package com.wigell.wigellcarrental.services;

import com.wigell.wigellcarrental.enums.CarStatus;
import com.wigell.wigellcarrental.exceptions.ConflictException;
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

import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
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
        testOrder = new Order(1L, LocalDate.of(2025,1,1),LocalDate.now().plusDays(1),LocalDate.now().plusDays(6), testCar, testCustomer, BigDecimal.valueOf(5000),true, false);
        testPrincipal = () -> testCustomer.getPersonalIdentityNumber();
    }


    //AA
    @Test
    void cancelOrderShouldSetIsActiveToFalse() {
        when(mockOrderRepository.findById(testOrder.getId())).thenReturn(Optional.of(testOrder));

        orderService.cancelOrder(1L, testPrincipal);

        assertFalse(testOrder.getIsActive());
        verify(mockOrderRepository).save(testOrder);

    }

    @Test
    void cancelOrderShouldSetIsCancelledToTrue() {
        when(mockOrderRepository.findById(testOrder.getId())).thenReturn(Optional.of(testOrder));

        orderService.cancelOrder(1L, testPrincipal);

        assertTrue(testOrder.getIsCancelled());
        verify(mockOrderRepository).save(testOrder);
    }

    //AA
    @Test
    void cancelOrderShouldSetCorrectCancellationFee(){
        when(mockOrderRepository.findById(testOrder.getId())).thenReturn(Optional.of(testOrder));

        long orderDaysLong = ChronoUnit.DAYS.between(testOrder.getStartDate(), testOrder.getEndDate());
        BigDecimal originalPrice = testOrder.getTotalPrice();
        orderService.cancelOrder(1L, testPrincipal);

        BigDecimal expectedFee = originalPrice
                .multiply(BigDecimal.valueOf(0.05))
                .multiply(BigDecimal.valueOf(orderDaysLong));

        assertEquals(0, expectedFee.compareTo(testOrder.getTotalPrice()));
        verify(mockOrderRepository).save(testOrder);
    }

    //AA
    @Test
    void cancelOrderShouldReturnStringMessageOrderWithIdIsCancelled(){
        when(mockOrderRepository.findById(testOrder.getId())).thenReturn(Optional.of(testOrder));

        String returnMessage = orderService.cancelOrder(1L, testPrincipal);
        BigDecimal fee = testOrder.getTotalPrice();

        assertEquals("Order with id '1' is cancelled, cancellation fee becomes: " + fee, returnMessage);
    }

    //AA
    @Test
    void cancelOrderShouldThrowResourceNotFoundExceptionIfOrderIdDoesNotExist(){
        Long missingOrderId = -1L;
        when(mockOrderRepository.findById(missingOrderId)).thenReturn(Optional.empty());

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class, () -> orderService.cancelOrder(missingOrderId, testPrincipal));

        assertEquals("Order not found with id: " + missingOrderId, e.getMessage());
    }

    //AA
    @Test
    void cancelOrderShouldThrowConflictExceptionIfOrderBelongsToAnotherCustomer(){
        Long orderId = 1L;

        Customer otherCustomer = new Customer(2L, "20000101-9999", "Ove", "Olsson", "ove@test.se", "070-1111111", "Falskgatan 1", List.of());
        Order foreignOrder = new Order(orderId, LocalDate.now().plusDays(1), LocalDate.now().plusDays(3), LocalDate.now(), testCar, otherCustomer, BigDecimal.valueOf(2000), true, false);

        when(mockOrderRepository.findById(orderId)).thenReturn(Optional.of(foreignOrder));

        ConflictException e = assertThrows(ConflictException.class, () -> orderService.cancelOrder(foreignOrder.getId(), testPrincipal));

        assertEquals("No order for '19890101-1234' with id: 1", e.getMessage());
    }

    //AA
    @Test
    void cancelOrderShouldThrowConflictExceptionIfOrderAlreadyStartedBeforeToday(){
        when(mockOrderRepository.findById(testOrder.getId())).thenReturn(Optional.of(testOrder));

        testOrder.setStartDate(LocalDate.now().minusDays(1));
        testOrder.setEndDate(LocalDate.now().plusDays(1));

        ConflictException e = assertThrows(ConflictException.class, () -> orderService.cancelOrder(testOrder.getId(), testPrincipal));

        assertEquals("Order has already started and can't then be cancelled", e.getMessage());

    }

    //AA
    @Test
    void cancelOrderShouldThrowConflictExceptionIfOrderStartsToday(){
        when(mockOrderRepository.findById(testOrder.getId())).thenReturn(Optional.of(testOrder));

        testOrder.setStartDate(LocalDate.now());
        testOrder.setEndDate(LocalDate.now().plusDays(3));

        ConflictException e = assertThrows(ConflictException.class, () -> orderService.cancelOrder(testOrder.getId(), testPrincipal));

        assertEquals("Order has already started and can't then be cancelled", e.getMessage());
    }

    //AA
    @Test
    void cancelOrderShouldThrowConflictExceptionIfOrderAlreadyEnded(){
        when(mockOrderRepository.findById(testOrder.getId())).thenReturn(Optional.of(testOrder));

        testOrder.setStartDate(LocalDate.now().minusDays(10));
        testOrder.setEndDate(LocalDate.now().minusDays(8));

        ConflictException e = assertThrows(ConflictException.class, () -> orderService.cancelOrder(testOrder.getId(), testPrincipal));

        assertEquals("Order has already ended", e.getMessage());

    }
    //AA
    @Test
    void cancelOrderShouldThrowConflictExceptionIfOrderEndsToday(){
        when(mockOrderRepository.findById(testOrder.getId())).thenReturn(Optional.of(testOrder));

        testOrder.setStartDate(LocalDate.now().minusDays(2));
        testOrder.setEndDate(LocalDate.now());

        ConflictException e = assertThrows(ConflictException.class, () -> orderService.cancelOrder(testOrder.getId(), testPrincipal));

        assertEquals("Order has already ended", e.getMessage());
    }


    //AA
    @Test
    void cancelOrderShouldThrowConflictExceptionIfOrderIsAlreadyCancelled(){
        when(mockOrderRepository.findById(testOrder.getId())).thenReturn(Optional.of(testOrder));

        testOrder.setIsCancelled(true);

        ConflictException e = assertThrows(ConflictException.class, () -> orderService.cancelOrder(testOrder.getId(), testPrincipal));

        assertEquals("Order is already cancelled", e.getMessage());
    }

}