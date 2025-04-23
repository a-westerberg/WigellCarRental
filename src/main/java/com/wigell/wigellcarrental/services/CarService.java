package com.wigell.wigellcarrental.services;

import com.wigell.wigellcarrental.entities.Car;

import java.util.List;

//SA
public interface CarService {
    //AWS
    List<Car> getAvailableCars();

    //AA
    String deleteCar(String input);

    //AA
    public List<Car> getAllCars();

    //AA
    Car addCar(Car car);
}
