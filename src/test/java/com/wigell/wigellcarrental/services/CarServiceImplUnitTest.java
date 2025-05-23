package com.wigell.wigellcarrental.services;

import com.wigell.wigellcarrental.enums.CarStatus;
import com.wigell.wigellcarrental.exceptions.InvalidInputException;
import com.wigell.wigellcarrental.exceptions.ResourceNotFoundException;
import com.wigell.wigellcarrental.exceptions.UniqueConflictException;
import com.wigell.wigellcarrental.models.entities.Car;
import com.wigell.wigellcarrental.models.entities.Order;
import com.wigell.wigellcarrental.repositories.CarRepository;
import com.wigell.wigellcarrental.repositories.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

//WIG-101-SJ
@ExtendWith(MockitoExtension.class)
class CarServiceImplUnitTest {

    @Mock
    private CarRepository mockCarRepository;
    @Mock
    private OrderRepository mockOrderRepository;

    private CarServiceImpl carService;

    private Car exsistingCar;
    private Principal principal;

    //WIG-101-SJ
    @BeforeEach
    void setUp() {
        carService = new CarServiceImpl(mockCarRepository, mockOrderRepository);

        exsistingCar = new Car(
                1L,
                "Volvo",
                "XC90",
                "VOL123",
                CarStatus.AVAILABLE,
                BigDecimal.valueOf(899.00),
                List.of()
        );

        principal = () -> "admin";

    }

    //WIG-101-SJ
    @Test
    void getAvailableCarsShouldReturnListOfAvailableCars() {
        when(mockCarRepository.findByStatus(CarStatus.AVAILABLE)).thenReturn(List.of(exsistingCar));

        List<Car> availableCars = carService.getAvailableCars();

        assertFalse(availableCars.isEmpty());
        assertEquals(1, availableCars.size());
        assertEquals(CarStatus.AVAILABLE, availableCars.get(0).getStatus());
        assertEquals("Volvo", availableCars.get(0).getMake());
    }

    //WIG-101-SJ
    @Test
    void getAvailableCarsShouldThrowExceptionIfNoCarsAvailable() {
        when(mockCarRepository.findByStatus(CarStatus.AVAILABLE)).thenReturn(List.of());
        assertThrows(ResourceNotFoundException.class, () -> carService.getAvailableCars());
    }

    //WIG-102-SJ
    @Test
    void updateCarShouldUpdateCarCorrectly() {

        Car updateRequest = new Car(
                1L,
                "Toyota",
                "Corolla",
                "NEW987",
                CarStatus.IN_SERVICE,
                BigDecimal.valueOf(499.00),
                List.of()
        );

        when(mockCarRepository.findById(exsistingCar.getId())).thenReturn(Optional.of(exsistingCar));

        when(mockCarRepository.save(any(Car.class))).thenAnswer(inv -> inv.getArgument(0));

        Car result = carService.updateCar(updateRequest, principal);

        assertEquals("Toyota", exsistingCar.getMake());
        assertEquals("Corolla", exsistingCar.getModel());
        assertEquals("NEW987", exsistingCar.getRegistrationNumber());
        assertEquals(CarStatus.IN_SERVICE, exsistingCar.getStatus());
        assertEquals(BigDecimal.valueOf(499.00), exsistingCar.getPricePerDay());

        verify(mockCarRepository).save(exsistingCar);

        assertSame(exsistingCar, result);

    }

    //WIG-102-SJ
    @Test
    void updateCarShouldThrowExceptionIfCarIdIsNull() {
        Car invalidCar = new Car();
        invalidCar.setMake("Volvo");

        InvalidInputException e = assertThrows(InvalidInputException.class, () -> carService.updateCar(invalidCar, principal));

        assertEquals("Invalid input: CAR [id] cannot be null.", e.getMessage());
    }

    //WIG-102-SJ
    @Test
    void updateCarShouldThrowIfCarNotFound(){
        when(mockCarRepository.findById(exsistingCar.getId())).thenReturn(Optional.empty());

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class, () -> carService.updateCar(exsistingCar, principal));

        assertEquals("Car not found with id: 1", e.getMessage());
    }

    //WIG-102-SJ
    @Test
    void updateCarShouldThrowIfRegistrationNumberTakenByAnotherCar(){
        Car conflictingCar = new Car(
                2L,
                "Toyota",
                "RAV4",
                "VOL1234",
                CarStatus.AVAILABLE,
                BigDecimal.valueOf(599.00),
                List.of()
        );

        Car updateRequest = new Car(
                1L,
                "Volvo",
                "XC60",
                "NEW1234",
                CarStatus.AVAILABLE,
                BigDecimal.valueOf(999.00),
                List.of()
        );

        when(mockCarRepository.findById(1L)).thenReturn(Optional.of(exsistingCar));
        when(mockCarRepository.findByRegistrationNumber("NEW1234")).thenReturn(Optional.of(conflictingCar));

        UniqueConflictException e = assertThrows(
                UniqueConflictException.class,
                () -> carService.updateCar(updateRequest, principal)
        );

        assertEquals("Registration number: {NEW1234} already exists, duplicates is not allowed.", e.getMessage());

    }

    //WIG-102-SJ
    @Test
    void updateCarShouldNotLogChangesIfNoFieldsAreChanged(){
        when(mockCarRepository.findById(exsistingCar.getId())).thenReturn(Optional.of(exsistingCar));
        when(mockCarRepository.save(any(Car.class))).thenReturn(exsistingCar);

        Car identicalCar = new Car(
                exsistingCar.getId(),
                exsistingCar.getMake(),
                exsistingCar.getModel(),
                exsistingCar.getRegistrationNumber(),
                exsistingCar.getStatus(),
                exsistingCar.getPricePerDay(),
                List.of()
        );

        Car updatedCar = carService.updateCar(identicalCar, principal);

        assertEquals(exsistingCar.getId(), updatedCar.getId());
    }

    //WIG-104-AWS
    @Test
    void deleteCarShouldDeleteCarById(){
        String input = "1";
        Principal principal = () -> "admin";
        Car car = new Car();
        car.setId(1L);
        car.setOrders(Collections.emptyList());

        when(mockCarRepository.findById(1L)).thenReturn(Optional.of(car));

        String result = carService.deleteCar(input, principal);

        verify(mockCarRepository).delete(car);
        assertEquals("Car  with id 1 deleted", result);
    }
    //WIG-104-AWS
    @Test
    void deleteCarShouldCallProcessOrderListWhenCarHasOrders(){
        String input = "1";
        Principal principal = () -> "admin";

        Car carWithOrders = new Car();
        carWithOrders.setId(1L);

        Order order = new Order();
        order.setStartDate(LocalDate.now().minusDays(5));
        order.setEndDate(LocalDate.now().minusDays(1));
        order.setCar(carWithOrders);

        carWithOrders.setOrders(List.of(order));

        when(mockCarRepository.findById(1L)).thenReturn(Optional.of(carWithOrders));

        String result = carService.deleteCar(input, principal);

        verify(mockCarRepository).delete(carWithOrders);
        assertEquals("Car  with id 1 deleted", result);
    }

    //WIG-104-AWS
    @Test
    void deleteCarShouldTriggerCatchBlockWhenCarNotFound(){
        String input = "1";
        Principal principal = () -> "admin";

        when(mockCarRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> carService.deleteCar(input, principal));
    }

    //WIG-104-AWS
    @Test
    void deleteCarShouldLogFailureWhenFindByRegistrationNumberThrows(){
        String input = "ABC123";
        Principal principal = () -> "admin";

        when(mockCarRepository.findByRegistrationNumber("ABC123"))
                .thenThrow(new RuntimeException("Mocked"));

        assertThrows(RuntimeException.class, () -> carService.deleteCar(input, principal));
    }


}