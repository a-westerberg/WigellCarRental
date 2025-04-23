package com.wigell.wigellcarrental.services;

import com.wigell.wigellcarrental.entities.Car;

import java.util.List;

//SA
public interface CarService {
    //AWS
    List<Car> getAvailableCars();

    //WIG-20-AA
    String deleteCar(String input);

    //WIG-17-AA
    List<Car> getAllCars();

    //WIG-18-AA
    Car addCar(Car car);
}
