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
                if(order.get().getStartDate().isBefore(LocalDate.now()) && order.get().getEndDate().isAfter(LocalDate.now())){
                    return "Order has already started and can't then be cancelled";

                } else if (order.get().getEndDate().isBefore(LocalDate.now())) {
                    return "Order has already ended";

                } else {
                    Order orderToCancel = order.get();
                    orderToCancel.setTotalPrice(MicroMethods.calculateCancellationFee(orderToCancel));
                    orderToCancel.setIsActive(false);
                    orderRepository.save(order.get());
                    return "Order with id '" + orderId + "' is cancelled";

                }
            }else {
                return "No order for '" + principal.getName() + "' with id: " + orderId;
            }

        }
        return "Order not found";
    }

    //SA
    @Override
    public String removeOrdersBeforeDate(LocalDate date, Principal principal) {
        List<Order>orders = orderRepository.findAllByEndDateBefore(date);
        if(orders.isEmpty()){
            return "No orders before "+date+" found";
        }
        orderRepository.deleteAll(orders);
        return "All orders before "+date+" removed";
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
                    orderToUpdate.getCar().setStatus(CarStatus.IN_SERVICE);
                    carRepository.save(orderToUpdate.getCar());
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
