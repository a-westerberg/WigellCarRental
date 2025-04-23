package com.wigell.wigellcarrental.services;

import com.wigell.wigellcarrental.entities.Car;
import com.wigell.wigellcarrental.enums.CarStatus;
import com.wigell.wigellcarrental.exceptions.InvalidInputException;
import com.wigell.wigellcarrental.exceptions.ResourceNotFoundException;
import com.wigell.wigellcarrental.exceptions.UniqueConflictException;
import com.wigell.wigellcarrental.repositories.CarRepository;
import com.wigell.wigellcarrental.services.utilities.MicroMethods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

//SA
@Service
public class CarServiceImpl implements CarService{
    // AWS
    private final CarRepository carRepository;

    // AWS
    @Autowired
    public CarServiceImpl(CarRepository carRepository) {
        this.carRepository = carRepository;
    }
    // AWS
    public List<Car> getAvailableCars() {
        return carRepository.findByStatus(CarStatus.AVAILABLE);
    }

    //AA
    public List<Car> getAllCars(){
        return carRepository.findAll();
    }

    //WIG-20-AA
    public String deleteCar(String input) {
        Car carToDelete = findCarToDelete(input);
        if (!carToDelete.getOrders().isEmpty()) {
            throw new RuntimeException("Cannot delete car with orders.");
            //TODO bygg bort så att denna kontroll inte ska behövas. Antingen koppla isär order och bil eller radera även ordrarna.
        }
        carRepository.delete(carToDelete);
        return  isInputId(input) ? "Car  with id " + input + " deleted" : "Car with registration number " + input + " deleted";
    }

    //WIG-18-AA
    public Car addCar(Car car) {
        validateAddCarInput(car);
        return carRepository.save(car);
    }

    //WIG-20-AA
    private boolean isInputId(String input) {
        if (input == null || input.isEmpty()) {
            throw new InvalidInputException("Car","Input", input);
        }
        return input.matches("\\d+");
    }

    //WIG-20-AA
    private Car findCarToDelete(String input) {
        if (isInputId(input)) {
            Long id = Long.parseLong(input);
            return carRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Car", "id", id));
        } else {
            return carRepository.findByRegistrationNumber(input).orElseThrow(() -> new ResourceNotFoundException("Car", "Registration Number", input));
        }
    }

    //WIG-18-AA
    private void validateAddCarInput(Car car) {
        MicroMethods.validateData("Car registration number", "registrationNumber", car.getRegistrationNumber());
        MicroMethods.validateData("Car status", "status", car.getStatus());
        MicroMethods.validateData("Car make", "make", car.getMake());
        MicroMethods.validateData("Car model", "model", car.getModel());
        MicroMethods.validateData("Price per day", "pricePerDay", car.getPricePerDay());

        checkUniqRegistrationNumber(car.getRegistrationNumber());
    }

    //WIG-18-AA
    private void checkUniqRegistrationNumber(String input) {
        Optional<Car> result = carRepository.findByRegistrationNumber(input);
        if (result.isPresent()) {
            throw new UniqueConflictException("Registration number",input);
        }
    }
}
