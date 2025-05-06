package com.wigell.wigellcarrental.services;

import com.wigell.wigellcarrental.exceptions.ConflictException;
import com.wigell.wigellcarrental.exceptions.InvalidInputException;
import com.wigell.wigellcarrental.models.entities.Car;
import com.wigell.wigellcarrental.models.entities.Customer;
import com.wigell.wigellcarrental.models.entities.Order;
import com.wigell.wigellcarrental.enums.CarStatus;
import com.wigell.wigellcarrental.exceptions.ResourceNotFoundException;
import com.wigell.wigellcarrental.models.valueobjects.*;
import com.wigell.wigellcarrental.repositories.CarRepository;
import com.wigell.wigellcarrental.repositories.CustomerRepository;
import com.wigell.wigellcarrental.repositories.OrderRepository;
import com.wigell.wigellcarrental.services.utilities.LogMethods;
import com.wigell.wigellcarrental.services.utilities.MicroMethods;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

//SA
@Service
public class OrderServiceImpl implements OrderService{
    //AWS
    private final OrderRepository orderRepository;
    private final CarRepository carRepository;
    private final CustomerRepository customerRepository;

    //WIG-71-AA
    private static final Logger USER_ANALYZER_LOGGER = LogManager.getLogger("userlog");

    //AWS
    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, CarRepository carRepository, CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.carRepository = carRepository;
        this.customerRepository = customerRepository;
    }
    //WIG-120-AWS
    @Override
    public List<Order> getActiveOrdersForCustomer(String personalIdentityNumber) {
        List<Order> orders = orderRepository.findByCustomer_PersonalIdentityNumberAndIsActiveTrue(personalIdentityNumber);

        if(orders.isEmpty()){
            throw new ResourceNotFoundException("List","active orders for customer", personalIdentityNumber);
        }
        return orders;
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
            USER_ANALYZER_LOGGER.warn("User {} tried to cancel an order which does not exist. " +
                    "\n\tID: {}",
                    principal.getName(), orderId);
            throw new ResourceNotFoundException("Order","id",orderId);
        }

        Order orderToCancel = optionalOrder.get();

        if(!orderToCancel.getCustomer().getPersonalIdentityNumber().equals(principal.getName())){
            USER_ANALYZER_LOGGER.warn("User {} tried to cancel an order they don't own. " +
                    "\n\tID: {}",
                    principal.getName(), orderId);
            return "No order for '" + principal.getName() + "' with id: " + orderId;
        }

        LocalDate today = LocalDate.now();

        if(orderToCancel.getStartDate().isBefore(today) && orderToCancel.getEndDate().isAfter(today)){
            USER_ANALYZER_LOGGER.warn("User {} tried to cancel and order that has already started. " +
                    "\n\tID: {}" +
                    "\n\tStart date: {}" +
                    "\n\tEnd date: {}" +
                    "\n\tToday: {}",
                    principal.getName(), orderId, orderToCancel.getStartDate(), orderToCancel.getEndDate(),today);
            return "Order has already started and can't then be cancelled";
        } else if (orderToCancel.getEndDate().isBefore(today)) {
            USER_ANALYZER_LOGGER.warn("User {} tried to cancel an order that has already happened." +
                    "\n\tID: {}" +
                    "\n\tEnd date: {}" +
                    "\n\tToday: {}",
                    principal.getName(), orderId, orderToCancel.getEndDate(),today);
            return "Order has already ended";
        }

        BigDecimal cancellationFee = calculateCancellationFee(orderToCancel);
        orderToCancel.setTotalPrice(cancellationFee);
        orderToCancel.setIsActive(false);

        orderRepository.save(orderToCancel);

        USER_ANALYZER_LOGGER.info("User '{}' cancelled order with ID '{}'. Cancellation fee: {}", principal.getName(), orderId, cancellationFee);
        return "Order with id '" + orderId + "' is cancelled";
    }

    //SA
    @Override
    public String removeOrdersBeforeDate(LocalDate date, Principal principal) {
        List<Order>orders = orderRepository.findAllByEndDateBeforeAndIsActiveFalse(date);

        if(orders.isEmpty()){
            USER_ANALYZER_LOGGER.warn("User {} tried to remove inactive orders before date, but no inactive order where found before: {}",
                    principal.getName(),date);
            return "Found no inactive orders before '"+date+"'";
        }

        orderRepository.deleteAll(orders);
        USER_ANALYZER_LOGGER.info("User '{}' removed all orders before '{}'", principal.getName(), date);
        return "All inactive orders before '"+date+"' has been removed";
    }

    // WIG-28-SJ
    @Override
    public Order addOrder(Order order, Principal principal) {

        try {
            validateOrder(order);
            constructOrder(order);

            if (!order.getCustomer().getPersonalIdentityNumber().equals(principal.getName())) {
                throw new ConflictException("You can't place orders for other customers.");
            }

            orderRepository.save(order);
            // WIG-89-SJ
            USER_ANALYZER_LOGGER.info("User '{}' has placed new order:{}",
                    principal.getName(),
                    LogMethods.logBuilder(order,
                            "id",
                            "bookedAt",
                            "startDate",
                            "endDate",
                            "isActive",
                            "totalPrice")
            );

            return order;

        } catch (Exception e) {
            USER_ANALYZER_LOGGER.warn("User '{}' failed to place order: {}",
                    principal.getName(),
                    LogMethods.logExceptionBuilder(order, e,
                            "id",
                            "bookedAt",
                            "startDate",
                            "endDate",
                            "isActive",
                            "totalPrice")
            );
            throw e;
        }
    }

    //SA
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

            if(!status.equals("away") && !status.equals("back") && !status.equals("service")){
                USER_ANALYZER_LOGGER.warn("User {} tried to update order '{}' but gave invalid status: {}", principal.getName(), orderId, status);
                return "Invalid status, there is 'away', 'back' and 'service'";
            }

            Order orderToUpdate = optionalOrder.get();
            boolean wasIsActive = orderToUpdate.getIsActive();
            CarStatus oldCarStatus = orderToUpdate.getCar().getStatus();

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
                    Car carToService = orderToUpdate.getCar();
                    carToService.setStatus(CarStatus.IN_SERVICE);
                    carRepository.save(carToService);

                }
            }

            USER_ANALYZER_LOGGER.info("User '{}' has updated order with ID '{}'" +
                            "\n\tOrder status: {} -> {}" +
                            "\n\tRegistation: {}" +
                            "\n\tCar status: {} -> {}",
                    principal.getName(),
                    orderId,
                    wasIsActive,
                    orderToUpdate.getIsActive().toString(),
                    orderToUpdate.getCar().getRegistrationNumber(),
                    oldCarStatus.toString(),
                    orderToUpdate.getCar().getStatus().toString());

            return "Order with id '" + orderId + "' has been updated" +
                    "\nOrder status: "+orderToUpdate.getIsActive().toString()+
                    "\nCar registration: " +orderToUpdate.getCar().getRegistrationNumber()+
                    "\nCar status: "+orderToUpdate.getCar().getStatus().toString();
        }
        USER_ANALYZER_LOGGER.warn("User {} tried to update an order which does not exist." +
                "\n\tID: {}",
                principal.getName(), orderId);
        throw new ResourceNotFoundException("Order","id",orderId);
    }

    @Override
    public String updateOrderCar(Long orderId, Long carId, Principal principal) {
        Optional<Order>optionalOrder = orderRepository.findById(orderId);
        Optional<Car>optionalCar = carRepository.findById(carId);

        if(optionalCar.isEmpty()){
            USER_ANALYZER_LOGGER.warn("User {} tried to update the car on an order but car with ID '{}' does not exist.", principal.getName(), carId);
            throw new ResourceNotFoundException("Car","id",carId);
        }
        if (optionalOrder.isEmpty()) {
            USER_ANALYZER_LOGGER.warn("User {} tried to update the car on an order but order with ID '{}' does not exist.", principal.getName(), orderId);
            throw new ResourceNotFoundException("Order","id",orderId);
        }
        if(!optionalCar.get().getStatus().equals(CarStatus.AVAILABLE)){
            USER_ANALYZER_LOGGER.warn("User {} tried to update the car on an order with order '{}' with car '{}', but car the status isn't available, it's: {}.", principal.getName(), orderId, carId,optionalCar.get().getStatus());
            return "Car with id '" + carId + "' is not available";
        }

        Order orderToUpdate = optionalOrder.get();
        Car carToUpdate = optionalCar.get();

        Car oldCar = orderToUpdate.getCar();

        orderToUpdate.setCar(carToUpdate);
        orderRepository.save(orderToUpdate);
        carRepository.save(carToUpdate);

        USER_ANALYZER_LOGGER.info("User '{}' updated order with ID '{}' " +
                        "\n\tCar ID: {} -> {}" +
                        "\n\tCar, registration number: {} -> {}",
                principal.getName(),
                orderId,
                oldCar.getId(),
                carToUpdate.getId(),
                oldCar.getRegistrationNumber(),
                carToUpdate.getRegistrationNumber());

        return "Updated order '" + orderId + "' to have car " + carToUpdate.getRegistrationNumber();

    }

    //WIG-85-AA, WIG-96-AA
    @Override
    public PopularBrandStats getPopularBrand(String startDate, String endDate) {
        LocalDate startPeriod = MicroMethods.parseStringToDate(startDate);
        LocalDate endPeriod = MicroMethods.parseStringToDate(endDate);

        List<Order> orders = getOrdersBetweenDates(startPeriod, endPeriod);
        if (orders.isEmpty()) {
            throw new ResourceNotFoundException("No orders found between " + startDate + " and " + endDate);
        }

        Map<String, Long> makeCountMap = countMakes(orders);
        Map<String,Long> sortedMap = MicroMethods.sortMapByValueThenKey(makeCountMap);

        return new PopularBrandStats(startPeriod, endPeriod, sortedMap);
    }

    // WIG-97-SJ
    @Override
    public AverageRentalPeriodStats getAverageRentalPeriod() {
        List<Order> allOrders = orderRepository.findAll();

        List<RentalPeriodDetails> rentalDetails = allOrders.stream()
                .map(order -> {
                    long days = ChronoUnit.DAYS.between(order.getStartDate(), order.getEndDate());
                    return new RentalPeriodDetails(order.getId(),order.getStartDate(), order.getEndDate(), days);
                })
                .toList();

        double average = rentalDetails.stream()
                .mapToLong(RentalPeriodDetails::getNumberOfDays)
                .average()
                .orElse(0.0);

        return new AverageRentalPeriodStats(average, rentalDetails);
    }

    // WIG-97-SJ
    @Override
    public AverageOrderCostStats costPerOrder() {
        List<Order> allOrders = orderRepository.findAll();

        List<OrderCostDetails> orderDetails = allOrders.stream()
                .map(order -> new OrderCostDetails(
                        order.getId(),
                        order.getCar().getId(),
                        order.getTotalPrice()
                ))
                .toList();

        BigDecimal total = orderDetails.stream()
                .map(OrderCostDetails::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal average = orderDetails.isEmpty()
                ? BigDecimal.ZERO
                : total.divide(BigDecimal.valueOf(orderDetails.size()), 2, RoundingMode.HALF_UP);

        return new AverageOrderCostStats(average, orderDetails);
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
    public Order constructOrder(Order order) {
        Long carId = order.getCar().getId();
        Long customerId = order.getCustomer().getId();

        Car car = carRepository.findById(carId).orElseThrow(()->new ResourceNotFoundException("Car", "id", carId));
        Customer customer = customerRepository.findById(customerId).orElseThrow(()->new ResourceNotFoundException("Customer", "id", customerId));

        order.setCar(car);
        order.setCustomer(customer);

        return order;
    }

    //WIG-AA-85
    private List<Order> getOrdersBetweenDates(LocalDate startDate, LocalDate endDate) {
        return orderRepository.findOverlappingOrders(startDate, endDate);
    }

    //WIG-85-AA
    private Map<String, Long> countMakes (List<Order> orders) {
        return orders.stream()
                .collect(Collectors.groupingBy(
                        order -> order.getCar().getMake(),
                        Collectors.counting()
                ));
    }

    //SA
    private static BigDecimal calculateCancellationFee(Order orderToCancel){
        long days = ChronoUnit.DAYS.between(orderToCancel.getStartDate(), orderToCancel.getEndDate());
        return orderToCancel.getTotalPrice().multiply(BigDecimal.valueOf(0.05).multiply(BigDecimal.valueOf(days)));
    }


    //WIG-25-AWS
    @Override
    public String removeOrderById(Long orderId, Principal principal) {
        try{
            Order orderToDelete = orderRepository.findById(orderId)
                    .orElseThrow(()->new ResourceNotFoundException("Order", "id", orderId));

            if(orderToDelete.getIsActive()){
                throw new ConflictException("Can't delete an active order");
            }

            orderRepository.delete(orderToDelete);

            USER_ANALYZER_LOGGER.info("User '{}' deleted order: {}'",
                    principal.getName(),
                    LogMethods.logBuilder(orderToDelete, "id", "startDate", "endDate", "isActive")
            );

            return "Order with ID '"+orderId+"' has been removed.";
        } catch (Exception e) {

            USER_ANALYZER_LOGGER.warn("User '{}' failed to remove order: {}",
                    principal.getName(),
                    LogMethods.logExceptionBuilder(Map.of("id", orderId), e)
            );
            throw e;
        }
    }

}
