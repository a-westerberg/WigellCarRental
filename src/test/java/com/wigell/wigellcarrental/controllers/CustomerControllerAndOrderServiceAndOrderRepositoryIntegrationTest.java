package com.wigell.wigellcarrental.controllers;

import com.wigell.wigellcarrental.enums.CarStatus;
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
import org.springframework.test.annotation.Rollback;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
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
        testOrder = orderRepository.save(new Order(null, LocalDate.of(2025,1,1),LocalDate.now(),LocalDate.now().plusDays(5), testCar, testCustomer, BigDecimal.valueOf(5000),true));

        testPrincipal = () -> testCustomer.getPersonalIdentityNumber();
    }


    @Test
    void cancelOrder() {
    }
}