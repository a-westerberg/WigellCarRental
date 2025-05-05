package com.wigell.wigellcarrental.services;

import com.wigell.wigellcarrental.models.entities.Car;
import com.wigell.wigellcarrental.models.entities.Order;
import com.wigell.wigellcarrental.enums.CarStatus;
import com.wigell.wigellcarrental.exceptions.ConflictException;
import com.wigell.wigellcarrental.exceptions.InvalidInputException;
import com.wigell.wigellcarrental.exceptions.ResourceNotFoundException;
import com.wigell.wigellcarrental.exceptions.UniqueConflictException;
import com.wigell.wigellcarrental.repositories.CarRepository;
import com.wigell.wigellcarrental.repositories.OrderRepository;
import com.wigell.wigellcarrental.services.utilities.LogMethods;
import com.wigell.wigellcarrental.services.utilities.MicroMethods;
import com.wigell.wigellcarrental.models.valueobjects.IncomeCar;
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
    // AWS TODO Exception
    public List<Car> getAvailableCars() {
        return carRepository.findByStatus(CarStatus.AVAILABLE);
    }

    //WIG-17-AA
    public List<Car> getAllCars(){
        return carRepository.findAll();
    }

    //WIG-20-AA
    public String deleteCar(String input, Principal principal) {
        Car carToDelete = findCarToDelete(input, principal);
        if (!carToDelete.getOrders().isEmpty()) {
            processOrderList(carToDelete.getOrders(), carToDelete.getId(), principal);
        }
        carRepository.delete(carToDelete);
        //TODO lägg in micrometod för att skapa loggningsmeddelandet
        USER_ANALYZER_LOGGER.info("A car with id {} and the registration number {} has been deleted", carToDelete.getId(), carToDelete.getRegistrationNumber());
        return  isInputId(input, principal) ? "Car  with id " + input + " deleted" : "Car with registration number " + input + " deleted";
    }

    //WIG-18-AA
    public Car addCar(Car car, Principal principal) {
        validateAddCarInput(car, principal,car.getId());
        //TODO lägg in micrometod för att skapa loggningsmeddelandet
        USER_ANALYZER_LOGGER.info("User: , {} Has added a car", principal.getName());
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
    private boolean isInputId(String input, Principal principal) {
        try {
            if (input == null || input.isEmpty()) {
                //TODO lägg in micrometod för att skapa loggningsmeddelandet
                USER_ANALYZER_LOGGER.warn("User: {} tried to delete car, but id/regisration number was null/empty", principal.getName());
                throw new InvalidInputException("Car", "Input", input);
            }
            return input.matches("\\d+");
        } catch (Exception e) {


            throw e;
        }
    }

    //WIG-20-AA
    private Car findCarToDelete(String input, Principal principal) {
        try {
            if (isInputId(input, principal)) {
                Long id = Long.parseLong(input);
                return carRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Car", "id", id));
            } else {
                return carRepository.findByRegistrationNumber(input).orElseThrow(() -> new ResourceNotFoundException("Car", "Registration Number", input));
            }
        } catch (Exception e) {
            Car placeHolder = new Car();
            String inputType;
            if (isInputId(input, principal)) {
                placeHolder.setId(Long.parseLong(input));
                inputType = "id";
            } else {
                placeHolder.setRegistrationNumber(input);
                inputType = "registrationNumber";
            }
            logCarDeleteFail(principal, inputType, placeHolder, e);
            throw e;
        }
    }

    //WIG-18-AA
    private void validateAddCarInput(Car car, Principal principal, Long id) {
        //TODO Hur gör vi med loggningsförsök i micro-metoderna? Skicka med principal och för vilken metod vi validerar datan?
        MicroMethods.validateData("Car registration number", "registrationNumber", car.getRegistrationNumber());
        MicroMethods.validateData("Car status", "status", car.getStatus());
        MicroMethods.validateData("Car make", "make", car.getMake());
        MicroMethods.validateData("Car model", "model", car.getModel());
        MicroMethods.validateData("Price per day", "pricePerDay", car.getPricePerDay());

        checkUniqRegistrationNumber(car.getRegistrationNumber(), principal, id);
    }

    //WIG-18-AA //WIG-83-AA
    private void checkUniqRegistrationNumber(String input, Principal principal, Long id) {
        try {
            Optional<Car> result = carRepository.findByRegistrationNumber(input);
            if (result.isPresent()) {
                throw new UniqueConflictException("Registration number", input);
            }
        } catch (Exception e) {
            Car placeHolder = new Car();
            placeHolder.setRegistrationNumber(input);
            placeHolder.setId(id);
            USER_ANALYZER_LOGGER.warn("User: {} failed to add car: {} ", principal.getName(), LogMethods.logExceptionBuilder(placeHolder,e,"id", "registrationNumber"));
            throw e;
        }
    }

    //WIG-37-AA //WIG-83-AA
    private void processOrderList(List<Order> orders, Long id, Principal principal) {
        LocalDate today = LocalDate.now();
        try {
            for (Order order : orders) {
                if (order.getEndDate().isBefore(today)) {
                    order.setCar(null);
                    orderRepository.save(order);
                }
                if (!today.isBefore(order.getStartDate()) && !today.isAfter(order.getEndDate())) {
                    throw new ConflictException("Car cannot be deleted due to ongoing booking.");
                }
                if (order.getStartDate().isAfter(today)) {
                    Car carToReplaceWith = carRepository.findFirstAvailableCarBetween(order.getStartDate(), order.getEndDate(), id).orElseThrow(() -> new ResourceNotFoundException("No replacement car available for the specified dates of upcoming order. Car cannot be deleted."));
                    order.setCar(carToReplaceWith);
                    orderRepository.save(order);
                }
            }
        } catch (Exception e) {
            Car placeHolder = new Car();
            placeHolder.setId(id);
            logCarDeleteFail(principal, "id",placeHolder, e);
            throw e;
        }
    }

    // WIG-24-AWS
    @Override
    public Car updateCar(Car car, Principal principal) {
        try{

            validateUpdateCarInput(car);

            Car existingCar = carRepository.findById(car.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Car", "id", car.getId()));

            if(!existingCar.getRegistrationNumber().equals(car.getRegistrationNumber())) {
                checkIfRegistrationNumberIsTakenByAnotherCar(car.getRegistrationNumber(), car.getId());
            }

            String changes = LogMethods.logUpdateBuilder(existingCar, car, "make", "model", "registrationNumber", "status", "pricePerDay");

            existingCar.setMake(car.getMake());
            existingCar.setModel(car.getModel());
            existingCar.setRegistrationNumber(car.getRegistrationNumber());
            existingCar.setStatus(car.getStatus());
            existingCar.setPricePerDay(car.getPricePerDay());

            Car updateCar = carRepository.save(existingCar);

            if(changes.isBlank()){
                USER_ANALYZER_LOGGER.info("User '{}' attempted to update car ID {}, but no changes were made.", principal.getName(), updateCar.getId());
            } else {
                USER_ANALYZER_LOGGER.info("User '{}' updated car ID {}: {}",
                        principal.getName(), updateCar.getId(), changes);
            }

            return updateCar;
        } catch (Exception e) {
            USER_ANALYZER_LOGGER.warn("User '{}' failed to update car: {}",
                    principal.getName(),
                    LogMethods.logExceptionBuilder(car, e, "id", "make", "model", "registrationNumber", "status", "pricePerDay")
            );
            throw e;
        }
    }

    // WIG-24-AWS
    private void validateUpdateCarInput(Car car){
        if(car.getId() == null) {
            throw new InvalidInputException("Car","id", null);
        }

        MicroMethods.validateData("Car", "make", car.getMake());
        MicroMethods.validateData("Car", "model", car.getModel());
        MicroMethods.validateData("Car", "registrationNumber", car.getRegistrationNumber());
        MicroMethods.validateData("Car", "status", car.getStatus());
        MicroMethods.validateData("Car", "pricePerDay", car.getPricePerDay());
    }
    // WIG-24-AWS
    private void checkIfRegistrationNumberIsTakenByAnotherCar(String regNumber, Long currentCarId) {
        Optional<Car> result = carRepository.findByRegistrationNumber(regNumber);
        if(result.isPresent() && !result.get().getId().equals(currentCarId)) {
            throw new UniqueConflictException("Registration number",regNumber);
        }
    }

    //WIG-83-AA
    private void logCarDeleteFail(Principal principal, String field, Car car, Exception e) {
        USER_ANALYZER_LOGGER.warn("User: {} failed to delete car: {}", principal.getName(), LogMethods.logExceptionBuilder(car, e, field));
    }

}
