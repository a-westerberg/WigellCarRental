package com.wigell.wigellcarrental.services;

import com.wigell.wigellcarrental.exceptions.ConflictException;
import com.wigell.wigellcarrental.exceptions.InvalidInputException;
import com.wigell.wigellcarrental.models.entities.Car;
import com.wigell.wigellcarrental.models.entities.Customer;
import com.wigell.wigellcarrental.models.entities.Order;
import com.wigell.wigellcarrental.enums.CarStatus;
import com.wigell.wigellcarrental.exceptions.ResourceNotFoundException;
import com.wigell.wigellcarrental.models.DTO.*;
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
import java.time.temporal.ChronoUnit;
import java.util.*;
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
        String exceptionReason = null;
        LocalDate today = LocalDate.now();
        Order orderToCancel = new Order();
        try {
            Optional<Order> optionalOrder = orderRepository.findById(orderId);
            if (optionalOrder.isEmpty()) {
                exceptionReason = "not found";
                throw new ResourceNotFoundException("Order", "id", orderId);
            }

            orderToCancel = optionalOrder.get();

            if (!orderToCancel.getCustomer().getPersonalIdentityNumber().equals(principal.getName())) {
                exceptionReason = "unauthorized";
                throw new ConflictException("No order for '" + principal.getName() + "' with id: " + orderId);
            }

            if(orderToCancel.getIsCancelled()){
                exceptionReason = "cancelled";
                throw new ConflictException("Order is already cancelled");
            }

            if (orderToCancel.getStartDate().isBefore(today) && orderToCancel.getEndDate().isAfter(today) || orderToCancel.getStartDate().isEqual(today)) {
                exceptionReason = "invalid date";
                throw new ConflictException("Order has already started and can't then be cancelled");

            } else if (orderToCancel.getEndDate().isBefore(today) || orderToCancel.getEndDate().isEqual(today)) {
                exceptionReason = "invalid date";
                throw new ConflictException("Order has already ended");
            }

            Map<String, Object> oldValues = Map.of(
                    "totalPrice",orderToCancel.getTotalPrice(),
                    "isActive",orderToCancel.getIsActive(),
                    "isCancelled",orderToCancel.getIsCancelled()
            );

            BigDecimal cancellationFee = calculateCancellationFee(orderToCancel);
            orderToCancel.setTotalPrice(cancellationFee);
            orderToCancel.setIsActive(false);
            orderToCancel.setIsCancelled(true);

            orderRepository.save(orderToCancel);

            Map<String, Object> newValues = Map.of(
                    "totalPrice",orderToCancel.getTotalPrice(),
                    "isActive",orderToCancel.getIsActive(),
                    "isCancelled",orderToCancel.getIsCancelled()
            );

            String change = LogMethods.logUpdateBuilder(
                    oldValues,newValues
            );

            USER_ANALYZER_LOGGER.info("User '{}' cancelled order: {}",
                    principal.getName(),
                    change);

            return "Order with id '" + orderId + "' is cancelled, cancellation fee becomes: "+cancellationFee;

        }catch (Exception e){
            Map<String, Object> changes = new HashMap<>();

            if(exceptionReason.equals("unauthorized") || exceptionReason.equals("not found")){
                changes = Map.of("id",orderId);
            } else if (exceptionReason.equals("invalid date")) {
                changes = Map.of(
                        "id", orderId,
                        "startDate",orderToCancel.getStartDate(),
                        "endDate",orderToCancel.getEndDate(),
                        "today",today);
            } else if (exceptionReason.equals("cancelled")) {
                changes = Map.of(
                        "isCancelled",orderToCancel.getIsCancelled()
                );
            }

            USER_ANALYZER_LOGGER.warn("User '{}' failed to canel order: {}",
                    principal.getName(),
                    LogMethods.logExceptionBuilder(changes, e));

            throw e;
        }

    }

    //SA
    @Override
    public String removeOrdersBeforeDate(LocalDate date, Principal principal) {
        try {
            List<Order> orders = orderRepository.findAllByEndDateBeforeAndIsActiveFalse(date);
            if (orders.isEmpty()) {
                throw new ResourceNotFoundException("Found no inactive orders before '" + date + "'");
            }
            orderRepository.deleteAll(orders);

            USER_ANALYZER_LOGGER.info("User '{}' removed all order before: {}",
                    principal.getName(),
                    LogMethods.logBuilder(Map.of("date",date)));

            return "All inactive orders before '" + date + "' has been removed";
        }catch (Exception e){
            USER_ANALYZER_LOGGER.warn("User '{}' failed to remove inactive orders before '{}'",
                    principal.getName(),
                    LogMethods.logExceptionBuilder(Map.of("date", date), e));
            throw e;
        }
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
        String exceptionReason = "";
        try {
            Optional<Order> optionalOrder = orderRepository.findById(orderId);
            if (optionalOrder.isPresent()) {

                if (!status.equals("away") && !status.equals("back") && !status.equals("service")) {
                    exceptionReason = "invalid status";
                    throw new ResourceNotFoundException("Invalid status '"+status+"'. There is 'away', 'back' and 'service'");
                }

                Order orderToUpdate = optionalOrder.get();

                Map<String, Object> oldValues = Map.of(
                        "isActive",orderToUpdate.getIsActive(),
                        "status",orderToUpdate.getCar().getStatus()
                );

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

                Map<String, Object> newValues = Map.of(
                        "isActive",orderToUpdate.getIsActive(),
                        "status",orderToUpdate.getCar().getStatus()
                );

                String change = LogMethods.logUpdateBuilder(
                        oldValues,newValues
                );

                USER_ANALYZER_LOGGER.info("User '{}' updated order status: {}",
                        principal.getName(),
                        change);

                return "Order with id '" + orderId + "' has been updated" +
                        "\nOrder status: " + orderToUpdate.getIsActive().toString() +
                        "\nCar registration: " + orderToUpdate.getCar().getRegistrationNumber() +
                        "\nCar status: " + orderToUpdate.getCar().getStatus().toString();
            }else {
                throw new ResourceNotFoundException("Order", "id", orderId);
            }

        }catch (Exception e){
            Map<String, Object> changes;
            if(exceptionReason.equals("invalid status")){
                changes = Map.of("status",status);
            }else {
                changes = Map.of("id",orderId);
            }

            USER_ANALYZER_LOGGER.warn("User '{}' failed to update order status: {}",
                    principal.getName(),
                    LogMethods.logExceptionBuilder(changes,e));
            throw e;

        }
    }

    //SA
    @Override
    public String updateOrderCar(Long orderId, Long carId, Principal principal) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        Optional<Car> optionalCar = carRepository.findById(carId);
        String exceptionReason = "";
        Car carToUpdate = new Car();

        try {
            if (optionalCar.isEmpty()) {
                exceptionReason = "car not found";
                throw new ResourceNotFoundException("Car", "id", carId);
            }
            if (optionalOrder.isEmpty()) {
                exceptionReason = "order not found";
                throw new ResourceNotFoundException("Order", "id", orderId);
            }

            Order orderToUpdate = optionalOrder.get();
            carToUpdate = optionalCar.get();

            if(orderToUpdate.getCar().getId().equals(carToUpdate.getId())){
                exceptionReason = "car already on order";
                throw new ConflictException("Car '"+carId+"' is already on order '" + orderId+"'");
            }

            if (!carToUpdate.getStatus().equals(CarStatus.AVAILABLE)) {
                exceptionReason = "car is not available";
                throw new ResourceNotFoundException("Car with id '" + carId + "' is not available");
            }

            Map<String, Object> oldValues = Map.of(
                    "car id",orderToUpdate.getCar().getId(),
                    "registrationNumber",orderToUpdate.getCar().getRegistrationNumber()
            );

            orderToUpdate.setCar(carToUpdate);
            orderRepository.save(orderToUpdate);
            carRepository.save(carToUpdate);

            Map<String, Object> newValues = Map.of(
                    "car id",orderToUpdate.getCar().getId(),
                    "registrationNumber",orderToUpdate.getCar().getRegistrationNumber()
            );

            String change = LogMethods.logUpdateBuilder(
                    oldValues,newValues
            );

            USER_ANALYZER_LOGGER.info("User '{}' updated car on order: {}",
                    principal.getName(),
                    change);

            return "Updated order '" + orderId + "' to have car " + carToUpdate.getRegistrationNumber();

        }catch (Exception e){
            Map<String, Object> changes = new HashMap<>();

            switch (exceptionReason) {
                case "order not found" -> changes = Map.of("id", orderId);

                case "car not found" -> changes = Map.of("id", carId);

                case "car is not available" -> changes = Map.of("car id", carId,
                        "status", carToUpdate.getStatus());
                case "car already on order" -> changes = Map.of("orderId",orderId,"carId",carId);
            }

            USER_ANALYZER_LOGGER.warn("User '{}' failed to update car on order: {}",
                    principal.getName(),
                    LogMethods.logExceptionBuilder(changes, e));
            throw e;

        }
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

        order.setIsCancelled(false);

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

    // WIG-114-AWS
    @Override
    public IncomeBetweenDatesDTO getIncomeOnMonth(String year, String month) {
        try{
            int y = Integer.parseInt(year);
            int m = Integer.parseInt(month);

            if(m<1 || m>12){
                throw new InvalidInputException("Month", "1-12", month);
            }

            LocalDate start = LocalDate.of(y, m, 1);
            LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

            return getIncomeBetweenDates(start.toString(), end.toString());
        } catch (NumberFormatException e){
            throw new InvalidInputException("Year/Month", "numeric", year+"/"+month);
        }
    }

    // WIG-114-AWS
    @Override
    public IncomeBetweenDatesDTO getIncomeBetweenDates(String start, String end) {
        LocalDate startDate = MicroMethods.parseStringToDate(start);
        LocalDate endDate = MicroMethods.parseStringToDate(end);

        if(startDate.isAfter(endDate)){
            throw new InvalidInputException("Date range", "start must be before end", start+" > "+end);
        }

        List<Order> orders = orderRepository.findAll().stream()
                .filter(order -> {
                    LocalDate date = order.getStartDate();
                    return !date.isBefore(startDate) && !date.isAfter(endDate);
                })
                .toList();
        if(orders.isEmpty()){
            throw new ResourceNotFoundException("Orders","start between", start + " and " + end);
        }

        BigDecimal total = orders.stream()
                .map(Order::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new IncomeBetweenDatesDTO(startDate, endDate, total);
    }

    // WIG-114-AWS
    @Override
    public IncomeBetweenDatesDTO getIncomeByYear(String year) {
       try{
           int y = Integer.parseInt(year);

           List<Order> orders = orderRepository.findAll().stream()
                   .filter(order -> order.getStartDate().getYear() == y)
                   .toList();

           if(orders.isEmpty()){
               throw new ResourceNotFoundException("Orders","year", year);
           }

           BigDecimal total = orders.stream()
                   .map(Order::getTotalPrice)
                   .reduce(BigDecimal.ZERO, BigDecimal::add);

           return new IncomeBetweenDatesDTO(LocalDate.of(y, 1, 1), LocalDate.of(y, 12, 31), total);

       } catch (NumberFormatException e){
           throw new InvalidInputException("Year", "numeric", year);
       }


    }

}
