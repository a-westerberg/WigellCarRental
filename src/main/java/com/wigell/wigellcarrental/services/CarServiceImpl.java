package com.wigell.wigellcarrental.services;

import com.wigell.wigellcarrental.entities.Car;
import com.wigell.wigellcarrental.entities.Order;
import com.wigell.wigellcarrental.enums.CarStatus;
import com.wigell.wigellcarrental.exceptions.ConflictException;
import com.wigell.wigellcarrental.exceptions.InvalidInputException;
import com.wigell.wigellcarrental.exceptions.ResourceNotFoundException;
import com.wigell.wigellcarrental.exceptions.UniqueConflictException;
import com.wigell.wigellcarrental.repositories.CarRepository;
import com.wigell.wigellcarrental.repositories.OrderRepository;
import com.wigell.wigellcarrental.services.utilities.MicroMethods;
import com.wigell.wigellcarrental.valueobjects.IncomeCar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

//SA
@Service
public class CarServiceImpl implements CarService{
    // AWS
    private final CarRepository carRepository;
    private final OrderRepository orderRepository;

    //WIG-71-AA
    private static final Logger USER_ANALYZER_LOGGER = LogManager.getLogger("userlog");

    // AWS
    @Autowired
    public CarServiceImpl(CarRepository carRepository, OrderRepository orderRepository) {
        this.carRepository = carRepository;
        this.orderRepository = orderRepository;
    }
    // AWS
    public List<Car> getAvailableCars() {
        return carRepository.findByStatus(CarStatus.AVAILABLE);
    }

    //WIG-17-AA
    public List<Car> getAllCars(){
        return carRepository.findAll();
    }

    //WIG-20-AA
    public String deleteCar(String input, Principal principal) {
        Car carToDelete = findCarToDelete(input);
        if (!carToDelete.getOrders().isEmpty()) {
            processOrderList(carToDelete.getOrders(), carToDelete.getId());
        }
        carRepository.delete(carToDelete);
        USER_ANALYZER_LOGGER.info("A car with id {} and the registration number {} has been deleted", carToDelete.getId(), carToDelete.getRegistrationNumber());
        return  isInputId(input) ? "Car  with id " + input + " deleted" : "Car with registration number " + input + " deleted";
    }

    //WIG-18-AA
    public Car addCar(Car car, Principal principal) {
        validateAddCarInput(car);
        return carRepository.save(car);
    }

    //SA
    @Override
    public List<IncomeCar> incomeOnCars() {
        List<Car> cars = carRepository.findAll();
        if(cars.isEmpty()) {
            throw new ResourceNotFoundException("List","cars",0);
        }

        List<IncomeCar> incomeCars = new ArrayList<>();

        for (Car car : cars) {
            BigDecimal totalIncome = BigDecimal.valueOf(0);
            for (Order order : car.getOrders()) {
                totalIncome = totalIncome.add(order.getTotalPrice());
            }

            incomeCars.add(new IncomeCar(car,car.getOrders().size(),totalIncome));
        }
        return incomeCars;
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

    //WIG-37-AA
    private void processOrderList(List<Order> orders, Long id) {
        LocalDate today = LocalDate.now();
        for (Order order : orders) {
            if (order.getEndDate().isBefore(today)) {
                order.setCar(null);
                orderRepository.save(order);
            }
            if (!today.isBefore(order.getStartDate()) && !today.isAfter(order.getEndDate())) {
                throw new ConflictException("Car cannot be deleted due to ongoing booking.");
            }
            if (order.getStartDate().isAfter(today)) {
                Car carToReplaceWith = carRepository.findFirstByStatusAndIdNot(CarStatus.AVAILABLE,id).orElseThrow(() ->new ResourceNotFoundException("Car","Car Status [Available]", "Cannot delete car"));
                order.setCar(carToReplaceWith);
                System.out.println(carToReplaceWith.toString());
                orderRepository.save(order);
            }
        }
    }

}
