package com.wigell.wigellcarrental.services;

import com.wigell.wigellcarrental.entities.Car;
import com.wigell.wigellcarrental.entities.Customer;
import com.wigell.wigellcarrental.entities.Order;
import com.wigell.wigellcarrental.exceptions.InvalidInputException;
import com.wigell.wigellcarrental.exceptions.ResourceNotFoundException;
import com.wigell.wigellcarrental.repositories.CarRepository;
import com.wigell.wigellcarrental.repositories.CustomerRepository;
import com.wigell.wigellcarrental.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

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
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isPresent()) {
            //TODO: fixa när säkerhets läggs in då principal är null just nu utan den
            if (order.get().getCustomer().getPersonalIdentityNumber().equals(principal.getName())) {
                Order orderToCancel = order.get();
                orderToCancel.setIsActive(false);
                orderRepository.save(order.get());
                return "Order with id '"+orderId+"' is cancelled";
            }else {
                return "No order for '" + principal.getName() + "' with id: " + orderId;
            }

        }
        return "Order not found";
    }

    // WIG-28-SJ
    @Override
    public Order addOrder(Order order) {
        validateOrder(order);
        constructOrder(order);
        return orderRepository.save(order);
    }

    // WIG-28-SJ
    public Order validateOrder(Order order) {
        validateData("Booking day", "bookedAt", order.getBookedAt());
        validateData("Start date", "startDate", order.getStartDate());
        validateData("End date", "endDate", order.getEndDate());
        validateData("Car ID", "car", order.getCar().getId());
        validateData("Customer ID", "customer", order.getCustomer().getId());
        validateData("Total price", "totalPrice", order.getTotalPrice());
        validateData("Status", "isActive", order.getIsActive());
        return order;
    }

    // WIG-28-SJ
    public <T> T validateData(String resource, String field, T value) {
        if (value == null) {
            throw new InvalidInputException(resource, field, value);
        }
        return value;
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
