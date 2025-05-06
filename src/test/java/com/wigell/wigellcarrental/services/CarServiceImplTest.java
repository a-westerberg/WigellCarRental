package com.wigell.wigellcarrental.services;

import com.wigell.wigellcarrental.enums.CarStatus;
import com.wigell.wigellcarrental.exceptions.ResourceNotFoundException;
import com.wigell.wigellcarrental.models.entities.Car;
import com.wigell.wigellcarrental.repositories.CarRepository;
import com.wigell.wigellcarrental.repositories.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

//WIG-101-SJ
@ExtendWith(MockitoExtension.class)
class CarServiceImplTest {

    private CarServiceImpl carService;

    @Mock
    private CarRepository mockCarRepository;
    @Mock
    private OrderRepository mockOrderRepository;

    private Car carAvailable;

    //WIG-101-SJ
    @BeforeEach
    void setUp() {
        carService = new CarServiceImpl(mockCarRepository, mockOrderRepository);

        carAvailable = new Car(
                1L,
                "Volvo",
                "XC90",
                "VOL1234",
                CarStatus.AVAILABLE,
                BigDecimal.valueOf(899.00),
                List.of()
        );
    }


    //WIG-101-SJ
    @Test
    void getAvailableCarsShouldReturnListOfAvailableCars() {
        when(mockCarRepository.findByStatus(CarStatus.AVAILABLE)).thenReturn(List.of(carAvailable));

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

    @Test
    void updateCarShouldUpdateCarCorrectly() {
    }
}