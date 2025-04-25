package com.wigell.wigellcarrental.controllers;

import com.wigell.wigellcarrental.entities.Order;
import com.wigell.wigellcarrental.entities.Car;
import com.wigell.wigellcarrental.entities.Customer;
import com.wigell.wigellcarrental.entities.Order;
import com.wigell.wigellcarrental.services.CarServiceImpl;
import com.wigell.wigellcarrental.services.CustomerServiceImpl;
import com.wigell.wigellcarrental.services.OrderService;
import com.wigell.wigellcarrental.services.OrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
/* import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder; */
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.security.Principal;
import java.util.List;

//SA
@RestController
@RequestMapping("/api/v1")
//@PreAuthorize("hasRole('USER')")
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

    //Services
    //SA / WIG-26-SJ
    @GetMapping("/cars")
    public ResponseEntity<List<Car>>allCars(){
        return ResponseEntity.ok(carService.getAvailableCars());
    }



    //SA / WIG-28-SJ
    @PostMapping("/addorder")
    public ResponseEntity<Order> addOrder(@RequestBody Order order){
        return new ResponseEntity<>(orderService.addOrder(order), HttpStatus.CREATED);
    }

    //SA
    @PutMapping("/cancelorder/{orderId}")//Avboka order
    public ResponseEntity<String> cancelOrder(@PathVariable Long orderId, Principal principal) {
        return ResponseEntity.ok(orderService.cancelOrder(orderId,principal));
    }


    //SA //AWS TODO Denna funkar inte försen vi har fixat security så kommer ligga utkommenderad. Även utkommenderat i import som behövs
    /** @GetMapping("/activeorders")//Se aktiva bokningar
    public ResponseEntity<List<Order>> getActiveOrders() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String personalIdentityNumber = auth.getName();

        List<Order> activeOrders = orderService.getActiveOrdersForCustomer(personalIdentityNumber);
        return ResponseEntity.ok(activeOrders);
    }*/

    /*
    //SA
    @GetMapping("/orders")//Se tidigare bokningar
    public ResponseEntity<List<Booking>>orders(){
        return ResponseEntity.ok(service.orders());
    }
    */

    //SA / WIG-29-SJ
    @PutMapping("/updateinfo")
    public ResponseEntity<Customer>updateInfo(@RequestBody Customer customer){
        return ResponseEntity.ok(customerService.updateCustomer(customer));
    }
}
