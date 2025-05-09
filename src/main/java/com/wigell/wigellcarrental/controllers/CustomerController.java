package com.wigell.wigellcarrental.controllers;

import com.wigell.wigellcarrental.models.entities.Order;
import com.wigell.wigellcarrental.models.entities.Car;
import com.wigell.wigellcarrental.models.entities.Customer;
import com.wigell.wigellcarrental.services.CarServiceImpl;
import com.wigell.wigellcarrental.services.CustomerServiceImpl;
import com.wigell.wigellcarrental.services.OrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.security.Principal;
import java.util.List;

//SA
@RestController
@RequestMapping("/api/v1")
public class CustomerController {

    //AWS / WIG-26-SJ
    private final OrderServiceImpl orderService;
    private final CarServiceImpl carService;
    private final CustomerServiceImpl customerService;

    @Autowired
    public CustomerController(OrderServiceImpl orderService, CarServiceImpl carService, CustomerServiceImpl customerService) {
        this.orderService = orderService;
        this.carService = carService;
        this.customerService = customerService;
    }

    //SA / WIG-26-SJ
    @GetMapping("/cars")
    public ResponseEntity<List<Car>>allCars(){
        return ResponseEntity.ok(carService.getAvailableCars());
    }

    //SA / WIG-28-SJ
    @PostMapping("/addorder")
    public ResponseEntity<Order> addOrder(@RequestBody Order order, Principal principal){
        return new ResponseEntity<>(orderService.addOrder(order, principal), HttpStatus.CREATED);
    }

    //SA
    @PutMapping("/cancelorder/{orderId}")
    public ResponseEntity<String> cancelOrder(@PathVariable Long orderId, Principal principal) {
        return ResponseEntity.ok(orderService.cancelOrder(orderId,principal));
    }


    //SA //AWS
    @GetMapping("/activeorders")
    public ResponseEntity<List<Order>> getActiveOrders(Principal principal) {
        String personalIdentityNumber = principal.getName();
        List<Order> activeOrders = orderService.getActiveOrdersForCustomer(personalIdentityNumber);
        return ResponseEntity.ok(activeOrders);
    }


    //SA //AA
    @GetMapping("/orders")
    public ResponseEntity<List<Order>>getOrders(Principal principal){
        return ResponseEntity.ok(customerService.getOrders(principal));
    }


    //SA / WIG-29-SJ
    @PutMapping("/updateinfo")
    public ResponseEntity<Customer>updateInfo(@RequestBody Customer customer, Principal principal) {
        return ResponseEntity.ok(customerService.updateCustomer(customer, principal));
    }
}
