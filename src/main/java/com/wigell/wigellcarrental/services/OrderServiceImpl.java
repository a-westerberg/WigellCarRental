package com.wigell.wigellcarrental.services;

import com.wigell.wigellcarrental.entities.Car;
import com.wigell.wigellcarrental.entities.Customer;
import com.wigell.wigellcarrental.entities.Order;
import com.wigell.wigellcarrental.enums.CarStatus;
import com.wigell.wigellcarrental.exceptions.ConflictException;
import com.wigell.wigellcarrental.exceptions.ResourceNotFoundException;
import com.wigell.wigellcarrental.repositories.CarRepository;
import com.wigell.wigellcarrental.repositories.CustomerRepository;
import com.wigell.wigellcarrental.repositories.OrderRepository;
import com.wigell.wigellcarrental.services.utilities.MicroMethods;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

//SA
@Service
public class OrderServiceImpl implements OrderService{
    //AWS
    private final OrderRepository orderRepository;
    private final CarRepository carRepository;
    private final CustomerRepository customerRepository;

    //AWS
    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, CarRepository carRepository, CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.carRepository = carRepository;
        this.customerRepository = customerRepository;
    }
    //AWS
    @Override
    public List<Order> getActiveOrdersForCustomer(String personalIdentityNumber) {
        return orderRepository.findByCustomer_PersonalIdentityNumberAndIsActiveTrue(personalIdentityNumber);
    }

    //SA
    @Override
    public List<Order> getActiveOrders() {
        if(orderRepository.findAllByIsActiveTrue().isEmpty()){
            throw new ResourceNotFoundException("List","active orders",0);
        }
        return orderRepository.findAllByIsActiveTrue();
    }
    //SA
    @Override
    public String cancelOrder(Long orderId, Principal principal) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if(optionalOrder.isEmpty()){
            return "Couldn't' find order with id: " + orderId;
        }

        Order orderToCancel = optionalOrder.get();
        //TODO: fixa när säkerhets läggs in då principal är null just nu utan den
        if(!orderToCancel.getCustomer().getPersonalIdentityNumber().equals(principal.getName())){
            return "No order for '" + principal.getName() + "' with id: " + orderId;
        }

        LocalDate today = LocalDate.now();
        if(orderToCancel.getStartDate().isBefore(today) && orderToCancel.getEndDate().isAfter(today)){
            return "Order has already started and can't then be cancelled";
        } else if (orderToCancel.getEndDate().isBefore(today)) {
            return "Order has already ended";
        }

        BigDecimal cancellationFee = MicroMethods.calculateCancellationFee(orderToCancel);
        orderToCancel.setTotalPrice(cancellationFee);
        orderToCancel.setIsActive(false);

        for(Order carOrder : orderToCancel.getCar().getOrders()){
            if(carOrder.getId().equals(orderToCancel.getId())){
                carOrder.setTotalPrice(cancellationFee);
                carOrder.setIsActive(false);
                break;
            }
        }
        for(Order customerOrder : orderToCancel.getCustomer().getOrder()){
            if(customerOrder.getId().equals(orderToCancel.getId())){
                customerOrder.setTotalPrice(cancellationFee);
                customerOrder.setIsActive(false);
                break;
            }
        }

        orderRepository.save(orderToCancel);
        carRepository.save(orderToCancel.getCar());
        customerRepository.save(orderToCancel.getCustomer());

        return "Order with id '" + orderId + "' is cancelled";
    }

    //SA
    @Override
    public String removeOrdersBeforeDate(LocalDate date, Principal principal) {
        List<Order>orders = orderRepository.findAllByEndDateBeforeAndIsActiveFalse(date);
        if(orders.isEmpty()){
            return "Found no inactive orders before '"+date+"'";
        }
        for (Order order : orders) {
            if(order.getCar() != null){
                order.getCar().getOrders().remove(order);
                carRepository.save(order.getCar());
            }
            if(order.getCustomer() != null){
                order.getCustomer().getOrder().remove(order);
                customerRepository.save(order.getCustomer());
            }

            orderRepository.save(order);
        }
        orderRepository.deleteAll(orders);
        return "All inactive orders before '"+date+"' has been removed";
    }

    // WIG-28-SJ
    @Override
    public Order addOrder(Order order) {
        validateOrder(order);
        constructOrder(order);
        return orderRepository.save(order);
    }

    @Override
    public List<Order> getAllOrdersHistory() {
                if(orderRepository.findAll().isEmpty()){
            throw new ResourceNotFoundException("List","orders",0);
        }
        return orderRepository.findAll();
    }

    //SA
    @Override
    public String updateOrderStatus(Long orderId, String status, Principal principal) {
        Optional<Order>optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isPresent()) {
            Order orderToUpdate = optionalOrder.get();

            if(!status.equals("away") && !status.equals("back") && !status.equals("service")){
                return "Invalid status, there is 'away', 'back' and 'service'";
            }

            //TODO: Ha en för service?
            //TODO: Add so if a car is in service it's later orders are changed to another car that is available under those orders time periods
            switch (status) {
                case "away" -> {
                    orderToUpdate.setIsActive(true);
                    orderRepository.save(orderToUpdate);
                    orderToUpdate.getCar().setStatus(CarStatus.BOOKED);
                    carRepository.save(orderToUpdate.getCar());
                }
                case "back" -> {
                    orderToUpdate.setIsActive(false);
                    orderRepository.save(orderToUpdate);
                    orderToUpdate.getCar().setStatus(CarStatus.AVAILABLE);
                    carRepository.save(orderToUpdate.getCar());
                }
                case "service" -> {
                    orderToUpdate.setIsActive(false);
                    orderRepository.save(orderToUpdate);

                    /*
                    1. Hämtar en order
                    2. Sätter den in inActive
                    3. Hämtar bilen den ordern har
                    4. Sätter bilen på service
                    5. Vill hämta andra ordrar denna bilen har
                    6. Vill ändra dem ordrarna
                    7. Hämtar en bil som har status AVAILABLE och som inte bokad under x-x datum
                    8. Vill på ordrar bilen hade byta ut den bilen till en annan, flytta ordrar till en annan bil
                    9. spara allt
                     */

                    Car carToService = orderToUpdate.getCar();
                    carToService.setStatus(CarStatus.IN_SERVICE);
                    carRepository.save(carToService);

                    /*if(!carToService.getOrders().isEmpty()) {
                        LocalDate startDate = orderToUpdate.getStartDate();
                        LocalDate endDate = orderToUpdate.getEndDate();
                        System.out.println("StartDate:"+startDate+"\nEndDate:"+endDate);

                        //Får fortfarande Volvo även fast den har en order 4/6-8/6 och en som använder bil som ska bytas ut har tid 2/6-5/6
                        *//*List<Car> availableCars = carRepository.findAvailableCarsForDateRange(
                                startDate, endDate, CarStatus.AVAILABLE
                        );*//*

                        List<Car>avCars = new ArrayList<>();
                        for(Car car : carRepository.findAll()){
                            if(car.getStatus() == CarStatus.AVAILABLE){
                                System.out.println("Car that has status Av------------------------------------------");
                                for(Order carOrder : car.getOrders()){
                                    if(carOrder.getIsActive().equals(true)){
                                        System.out.println("Active orders on car--------------------");
                                        //TODO: fix so booked cars can't be used, can be further down
                                        boolean isBooked = false;

                                        if (carOrder.getStartDate().isBefore(startDate)) {
                                            if (carOrder.getEndDate().isBefore(endDate)) {
                                                System.out.println("Car booked1");
                                                isBooked = true;
                                            } else if (carOrder.getEndDate().isAfter(endDate)) {
                                                System.out.println("Car booked2");
                                                isBooked = true;
                                            }
                                        }

                                        if (carOrder.getStartDate().isAfter(startDate) && carOrder.getEndDate().isAfter(endDate)) {
                                            if (carOrder.getStartDate().isBefore(endDate)) {
                                                System.out.println("Car booked4");
                                                isBooked = true;
                                            }
                                        }

                                        if (carOrder.getStartDate().isAfter(startDate) && carOrder.getEndDate().isBefore(endDate)) {
                                            System.out.println("Car booked3");
                                            isBooked = true;
                                        }


                                        if (!isBooked) {
                                            System.out.println("Av car");
                                            avCars.add(car);
                                            System.out.println("Av car size:" + avCars.size());
                                            break;
                                        }




                                    }

                                }

                            }
                        }

                        for (Car car : avCars) {
                            System.out.println("Available car: " + car.getRegistrationNumber());
                        }

                        if (avCars.isEmpty()) {
                            throw new ConflictException("There are other order that this car is booked to but there are not other cars available");
                        }


                        Random random = new Random();
                        for (Order carOrder : carToService.getOrders()) {

                            if(carOrder.getIsActive().equals(true) && carOrder.getStartDate().isAfter(LocalDate.now())){

                                Car replacement = avCars.get(random.nextInt(avCars.size()));
                                carOrder.setCar(replacement);
                                long days = ChronoUnit.DAYS.between(carOrder.getStartDate(), carOrder.getEndDate());
                                carOrder.setTotalPrice(replacement.getPricePerDay().multiply(BigDecimal.valueOf(days)));
                                orderRepository.save(carOrder);
                            }
                        }
                    }
                    */


                }
            }

            return "Order with id '" + orderId + "' has been updated" +
                    "\nOrder status: "+orderToUpdate.getIsActive().toString()+
                    "\nCar registration: " +orderToUpdate.getCar().getRegistrationNumber()+
                    "\nCar status: "+orderToUpdate.getCar().getStatus().toString()
                    ;
        }
        return "Order with id '"+orderId+"' not found";
    }

    @Override
    public String updateOrderCar(Long orderId, Long carId, Principal principal) {
        Optional<Order>optionalOrder = orderRepository.findById(orderId);
        Optional<Car>optionalCar = carRepository.findById(carId);
        if(optionalOrder.isPresent() && optionalCar.isPresent()){
            if(optionalCar.get().getStatus().equals(CarStatus.AVAILABLE)) {
                Order orderToUpdate = optionalOrder.get();
                Car carToUpdate = optionalCar.get();
                orderToUpdate.setCar(carToUpdate);
                orderRepository.save(orderToUpdate);
                carRepository.save(carToUpdate);
                return "Updated order:" + orderId + " to have car " + carToUpdate.getRegistrationNumber();
            }else {
                return "Car with id '"+carId+"' is not available";
            }
        }
        return "Could not find car or order with those id:\nOrder:"+orderId+"\nCar:"+carId;
    }

    // WIG-28-SJ
    public Order validateOrder(Order order) {
        MicroMethods.validateData("Booking day", "bookedAt", order.getBookedAt());
        MicroMethods.validateData("Start date", "startDate", order.getStartDate());
        MicroMethods.validateData("End date", "endDate", order.getEndDate());
        MicroMethods.validateData("Car ID", "car", order.getCar().getId());
        MicroMethods.validateData("Customer ID", "customer", order.getCustomer().getId());
        MicroMethods.validateData("Total price", "totalPrice", order.getTotalPrice());
        MicroMethods.validateData("Status", "isActive", order.getIsActive());
        return order;
    }

    // WIG-28-SJ
    public Order constructOrder(Order order){
        Long carId = order.getCar().getId();
        Long customerId = order.getCustomer().getId();

        Car car = carRepository.findById(carId).orElseThrow(()->new ResourceNotFoundException("Car", "id", carId));
        Customer customer = customerRepository.findById(customerId).orElseThrow(()->new ResourceNotFoundException("Customer", "id", customerId));

        order.setCar(car);
        order.setCustomer(customer);

        return order;
    }


}
