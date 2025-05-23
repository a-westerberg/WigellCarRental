package com.wigell.wigellcarrental.services;

import com.wigell.wigellcarrental.models.entities.Car;
import com.wigell.wigellcarrental.models.DTO.IncomeCarDTO;

import java.security.Principal;
import java.util.List;

//SA
public interface CarService {
    //AWS
    List<Car> getAvailableCars();

    //WIG-20-AA
    String deleteCar(String input, Principal principal);

    //WIG-17-AA
    List<Car> getAllCars();

    //WIG-18-AA
    Car addCar(Car car, Principal principal);

    //SA
    List<IncomeCarDTO> incomeOnCars();

    //WIG-24-AWS
    Car updateCar(Car car, Principal principal);
}
