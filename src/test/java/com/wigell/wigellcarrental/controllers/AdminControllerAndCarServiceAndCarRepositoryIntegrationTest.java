package com.wigell.wigellcarrental.controllers;

import com.wigell.wigellcarrental.enums.CarStatus;
import com.wigell.wigellcarrental.exceptions.InvalidInputException;
import com.wigell.wigellcarrental.exceptions.ResourceNotFoundException;
import com.wigell.wigellcarrental.models.entities.Car;
import com.wigell.wigellcarrental.repositories.CarRepository;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//WIG-105-SJ
@SpringBootTest
@Transactional
@Rollback
class AdminControllerAndCarServiceAndCarRepositoryIntegrationTest {

    private final AdminController adminController;
    private final CarRepository carRepository;
    private final OrderRepository orderRepository;

    private Car testCar;
    private Principal testPrincipal;

    @Autowired
    AdminControllerAndCarServiceAndCarRepositoryIntegrationTest(AdminController adminController, CarRepository carRepository, OrderRepository orderRepository) {
        this.adminController = adminController;
        this.carRepository = carRepository;
        this.orderRepository = orderRepository;
    }

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

    @Test
    void getAllAvailableCarsShouldReturnListWithAvailableCars() {
        ResponseEntity<List<Car>> response = adminController.getAllAvailableCars();
        List<Car> cars = response.getBody();

        assertNotNull(cars);
        assertFalse(cars.isEmpty());
        assertTrue(cars.stream().anyMatch(car -> car.getRegistrationNumber().equals("ABC123")));
    }

    @Test
    void getAllAvailableCarsShouldThrowIfNoneAreAvailable() {
        orderRepository.deleteAll();
        carRepository.deleteAll();

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class, () -> adminController.getAllAvailableCars());
        assertEquals("List not found with cars with status: AVAILABLE", e.getMessage());
    }

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
}

