package com.wigell.wigellcarrental.services;

import com.wigell.wigellcarrental.entities.Car;
import com.wigell.wigellcarrental.entities.Customer;
import com.wigell.wigellcarrental.entities.Order;
import com.wigell.wigellcarrental.enums.CarStatus;
import com.wigell.wigellcarrental.exceptions.ResourceNotFoundException;
import com.wigell.wigellcarrental.repositories.CarRepository;
import com.wigell.wigellcarrental.repositories.CustomerRepository;
import com.wigell.wigellcarrental.repositories.OrderRepository;
import com.wigell.wigellcarrental.services.utilities.MicroMethods;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
