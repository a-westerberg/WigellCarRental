package com.wigell.wigellcarrental.controllers;

import com.wigell.wigellcarrental.enums.CarStatus;
import com.wigell.wigellcarrental.exceptions.ConflictException;
import com.wigell.wigellcarrental.exceptions.ResourceNotFoundException;
import com.wigell.wigellcarrental.models.entities.Car;
import com.wigell.wigellcarrental.models.entities.Customer;
import com.wigell.wigellcarrental.models.entities.Order;
import com.wigell.wigellcarrental.repositories.CarRepository;
import com.wigell.wigellcarrental.repositories.CustomerRepository;
import com.wigell.wigellcarrental.repositories.OrderRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;



import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

//WIG-44-AA
@SpringBootTest
@Transactional
@Rollback
class CustomerControllerAndOrderServiceAndOrderRepositoryIntegrationTest {

    private final OrderRepository orderRepository;
    private final CarRepository carRepository;
    private final CustomerRepository customerRepository;
    private final CustomerController customerController;

    //AA
    private Car testCar;
    private Customer testCustomer;
    private Principal testPrincipal;
    private Order testOrder;

    //AA
    @Autowired
    CustomerControllerAndOrderServiceAndOrderRepositoryIntegrationTest(OrderRepository orderRepository, CarRepository carRepository, CustomerRepository customerRepository, CustomerController customerController) {
        this.orderRepository = orderRepository;
        this.carRepository = carRepository;
        this.customerRepository = customerRepository;
        this.customerController = customerController;
    }

    //AA
    @BeforeEach
    public void setUp(){

        testCar = carRepository.save(new Car(null, "Volvo", "V90","ABC789", CarStatus.AVAILABLE, BigDecimal.valueOf(1000), List.of()));
        testCustomer = customerRepository.save(new Customer(null, "19890101-1234", "Anna", "Andersson", "anna@test.se", "070-1234567", "Solrosvägen 1, 90347 Umeå", List.of()));
        testOrder = orderRepository.save(new Order(null, LocalDate.of(2025,1,1),LocalDate.now().plusDays(1),LocalDate.now().plusDays(6), testCar, testCustomer, BigDecimal.valueOf(5000),true,false));

        testPrincipal = () -> testCustomer.getPersonalIdentityNumber();
    }


    @Test
    void cancelOrderShouldReturnResponseEntityOkWithStringAndSetOrderToCancelledAndInactive() {
        BigDecimal originalPrice = testOrder.getTotalPrice();
        ResponseEntity<String> response = customerController.cancelOrder(testOrder.getId(),testPrincipal);

        long orderDaysLong = ChronoUnit.DAYS.between(testOrder.getStartDate(), testOrder.getEndDate());


        BigDecimal expectedFee = originalPrice
                .multiply(BigDecimal.valueOf(0.05))
                .multiply(BigDecimal.valueOf(orderDaysLong));

        String expectedResponseString = "Order with id '" + testOrder.getId() + "' is cancelled, cancellation fee becomes: " + expectedFee;

        Order canceledOrder = orderRepository.findById(testOrder.getId()).orElseThrow(() -> new IllegalStateException("Order not found after cancellation"));

        assertEquals(expectedResponseString, response.getBody());
        assertThat(response.getStatusCode().isSameCodeAs(HttpStatus.OK)).isTrue();
        assertThat(canceledOrder.getIsActive()).isFalse();
        assertThat(canceledOrder.getIsCancelled()).isTrue();
    }

    @Test
    void cancelOrderShouldThrowResourceNotFoundIfOrderDoesNotExist() {
        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class, () -> customerController.cancelOrder(-1L,testPrincipal));
        assertEquals("Order not found with id: " + -1L, e.getMessage());
    }

    @Test
    void cancelOrderShouldThrowConflictExceptionIfOrderBelongsToAnotherCustomer() {
        Customer anotherTestCustomer = customerRepository.save(new Customer(null, "19890201-5678", "Erik", "Eriksson", "erik@test.se", "070-1234568", "Solrosvägen 1, 90347 Umeå", List.of()));
        Order anotherCustomersOrder = orderRepository.save(new Order(null, LocalDate.of(2025,1,1),LocalDate.now().plusDays(10),LocalDate.now().plusDays(15), testCar, anotherTestCustomer, BigDecimal.valueOf(5000),true, false));

        ConflictException e = assertThrows(ConflictException.class, () -> customerController.cancelOrder(anotherCustomersOrder.getId(),testPrincipal));
        String expectedMessage = "No order for '" + testPrincipal.getName() + "' with id: " + anotherCustomersOrder.getId();

        assertEquals(expectedMessage, e.getMessage());
    }

    @Test
    void cancelOrderShouldThrowConflictExceptionIfOrderAlreadyStarted(){
        testOrder.setStartDate(LocalDate.now().minusDays(1));
        testOrder.setEndDate(LocalDate.now().plusDays(4));
        orderRepository.save(testOrder);
        ConflictException e = assertThrows(ConflictException.class, () -> customerController.cancelOrder(testOrder.getId(),testPrincipal));

        String expectedMessage = "Order has already started and can't then be cancelled";

        assertEquals(expectedMessage, e.getMessage());
    }

    @Test
    void cancelOrderShouldThrowConflictExceptionIfOrderAlreadyEnded(){
        testOrder.setStartDate(LocalDate.now().minusDays(6));
        testOrder.setEndDate(LocalDate.now().minusDays(1));
        orderRepository.save(testOrder);
        ConflictException e = assertThrows(ConflictException.class, () -> customerController.cancelOrder(testOrder.getId(),testPrincipal));

        String expectedMessage = "Order has already ended";
        assertEquals(expectedMessage, e.getMessage());
    }

}
