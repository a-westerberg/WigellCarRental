package com.wigell.wigellcarrental.controllers;

import com.wigell.wigellcarrental.enums.CarStatus;
import com.wigell.wigellcarrental.exceptions.InvalidInputException;
import com.wigell.wigellcarrental.exceptions.ResourceNotFoundException;
import com.wigell.wigellcarrental.models.entities.Car;
import com.wigell.wigellcarrental.models.entities.Customer;
import com.wigell.wigellcarrental.models.entities.Order;
import com.wigell.wigellcarrental.repositories.CarRepository;
import com.wigell.wigellcarrental.repositories.CustomerRepository;
import com.wigell.wigellcarrental.repositories.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//WIG-105-SJ
@SpringBootTest
@Transactional
@Rollback
class AdminCarFlowIntegrationTest {

    private final AdminController adminController;
    private final CarRepository carRepository;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;

    private Car testCar;
    private Principal testPrincipal;

    @Autowired
    AdminCarFlowIntegrationTest(AdminController adminController, CarRepository carRepository, OrderRepository orderRepository, CustomerRepository customerRepository) {
        this.adminController = adminController;
        this.carRepository = carRepository;
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
    }

    //WIG-105-SJ
    @BeforeEach
    void setUp() {
        testCar = carRepository.save(new Car(
                null,
                "Volvo",
                "XC60",
                "ABC123",
                CarStatus.AVAILABLE,
                BigDecimal.valueOf(799),
                List.of())
        );
        testPrincipal = () -> "admin";
    }

    //WIG-105-SJ
    @Test
    void getAllAvailableCarsShouldReturnListWithAvailableCars() {
        ResponseEntity<List<Car>> response = adminController.getAllAvailableCars();
        List<Car> cars = response.getBody();

        assertNotNull(cars);
        assertFalse(cars.isEmpty());
        assertTrue(cars.stream().anyMatch(car -> car.getRegistrationNumber().equals("ABC123")));
    }

    //WIG-105-SJ
    @Test
    void getAllAvailableCarsShouldThrowIfNoneAreAvailable() {
        orderRepository.deleteAll();
        carRepository.deleteAll();

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class, adminController::getAllAvailableCars);
        assertEquals("List not found with cars with status: AVAILABLE", e.getMessage());
    }

    //WIG-108-SJ
    @Test
    void updateCarShouldReturnUpdatedCarWithNewAttributes() {
        Car updateRequest = new Car(
                testCar.getId(),
                "Toyota",
                "Corolla",
                "NEW123",
                CarStatus.IN_SERVICE,
                BigDecimal.valueOf(750),
                List.of()
        );

        ResponseEntity<Car> response = adminController.updateCar(updateRequest, testPrincipal);

        Car updatedCar = carRepository.findById(updateRequest.getId()).orElseThrow();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updateRequest.getMake(), updatedCar.getMake());
        assertEquals(updateRequest.getModel(), updatedCar.getModel());
        assertEquals(updateRequest.getRegistrationNumber(), updatedCar.getRegistrationNumber());
        assertEquals(updateRequest.getStatus(), updatedCar.getStatus());
        assertEquals(updateRequest.getPricePerDay(), updatedCar.getPricePerDay());
    }

    //WIG-108-SJ
    @Test
    void updateCarShouldThrowIfIdIsNull(){
        Car invalidCar = new Car(
                null,
                "Opel",
                "Astra",
                "OPL123",
                CarStatus.AVAILABLE,
                BigDecimal.valueOf(500),
                List.of()
        );

        InvalidInputException e = assertThrows(InvalidInputException.class, () -> adminController.updateCar(invalidCar, testPrincipal));

        assertEquals("Invalid input: CAR [id] cannot be null.", e.getMessage());
    }

    //WIG-107-AWS
    @Test
    void removeCarByIdShouldSucceed(){
        String input = String.valueOf(testCar.getId());

        ResponseEntity<String> response = adminController.removeCar(input, testPrincipal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Car with id " + input + " deleted", response.getBody());
        assertTrue(carRepository.findById(testCar.getId()).isEmpty());
    }

    //WIG-107-AWS
    @Test
    void removeCarByRegistrationNumberShouldSucceed(){
        orderRepository.deleteAll();
        carRepository.deleteAll();

        Car freshCar = new Car(
                null,
                "Volvo",
                "XC60",
                "ABC123",
                CarStatus.AVAILABLE,
                BigDecimal.valueOf(799),
                List.of()
        );
        Car savedCar = carRepository.save(freshCar);
        String input = savedCar.getRegistrationNumber();

        ResponseEntity<String> response = adminController.removeCar(input, testPrincipal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Car with registration number " + input + " deleted", response.getBody());
        assertTrue(carRepository.findByRegistrationNumber(input).isEmpty());
    }

    //WIG-107-AWS
    @Test
    void removeCarWithOrdersShouldProcessOrdersAndDeleteCar(){
        Customer orderCustomer = customerRepository.save(new Customer(null,"19790909-9999", "Test", "Testson", "test@example.com", "0701234567", "Testvägen 1", List.of()));

        orderRepository.save(new Order(
                null,
                LocalDate.now().minusDays(10),
                LocalDate.now().minusDays(5),
                LocalDate.now().minusDays(1),
                testCar,
                orderCustomer,
                new BigDecimal("1500"),
                true,
                false
        ));

        String input = String.valueOf(testCar.getId());

        ResponseEntity<String> response = adminController.removeCar(input, testPrincipal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("deleted"));
        assertTrue(carRepository.findById(testCar.getId()).isEmpty());
    }

    //WIG-107-AWS
    @Test
    void removeCarByNonExistingIdShouldThrowException(){
        String input = "9999999";

        Exception e = assertThrows(Exception.class,
                () -> adminController.removeCar(input, testPrincipal));

        assertTrue(e.getMessage().contains("Car not found with id: " + input), e.getMessage());
    }


    //WIG-107-AWS
    @Test
    void removeCarByNonExistingRegistrationNumberShouldThrowException(){
        String input = "NOPE123";

        Exception e = assertThrows(Exception.class, () -> adminController.removeCar(input, testPrincipal));

        assertTrue(e.getMessage().contains("Car not found with Registration Number: " + input), e.getMessage());
    }

    //WIG-107-AWS
    @Test
    void removeCarWithInvalidIdFormatShouldTriggerResourceNotFoundException(){
        String input = "NOTANUMBER";

        Exception e = assertThrows(ResourceNotFoundException.class, () -> adminController.removeCar(input, testPrincipal));

        assertTrue(e.getMessage().contains("Car not found with Registration Number"), e.getMessage());
    }


}

