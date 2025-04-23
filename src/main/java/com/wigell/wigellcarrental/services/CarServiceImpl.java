package com.wigell.wigellcarrental.services;

import com.wigell.wigellcarrental.entities.Car;
import com.wigell.wigellcarrental.enums.CarStatus;
import com.wigell.wigellcarrental.exceptions.InvalidInputException;
import com.wigell.wigellcarrental.exceptions.ResourceNotFoundException;
import com.wigell.wigellcarrental.repositories.CarRepository;
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

    //AA
    public String deleteCar(String input) {
        Car carToDelete = findCarToDelete(input);
        if (!carToDelete.getOrders().isEmpty()) {
            throw new RuntimeException("Cannot delete car with active orders.");
            //TODO bygg bort så att denna kontroll inte ska behövas. Antingen koppla isär order och bil eller radera även ordrarna.
        }
        carRepository.delete(carToDelete);
        return  isInputId(input) ? "Car  with id " + input + " deleted" : "Car with registration number " + input + " deleted";
    }

    //AA
    public Car addCar(Car car) {

        return carRepository.save(car);
    }

    //AA
    private boolean isInputId(String input) {
        if (input == null || input.isEmpty()) {
            throw new InvalidInputException("Car","Input", input);
        }
        return input.matches("\\d+");
    }

    //AA
    private Car findCarToDelete(String input) {
        if (isInputId(input)) {
            Long id = Long.parseLong(input);
            return carRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Car", "id", id));
        } else {
            return carRepository.findByRegistrationNumber(input).orElseThrow(() -> new ResourceNotFoundException("Car", "Registration Number", input));
        }
    }
}
