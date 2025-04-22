package com.wigell.wigellcarrental.controllers;

import com.wigell.wigellcarrental.entities.Car;
import com.wigell.wigellcarrental.entities.Customer;
import com.wigell.wigellcarrental.entities.Order;
import com.wigell.wigellcarrental.services.CarServiceImpl;
import com.wigell.wigellcarrental.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
/* import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder; */
import org.springframework.web.bind.annotation.*;


import java.util.List;

//SA
@RestController
@RequestMapping("/api/v1")
//@PreAuthorize("USER")
public class CustomerController {

    //AWS / WIG-26-SJ
    private final OrderService orderService;
    private final CarServiceImpl carService;

    @Autowired
    public CustomerController(OrderService orderService, CarServiceImpl carService) {
        this.orderService = orderService;
        this.carService = carService;
    }

    //Services
    //SA / WIG-26-SJ
    @GetMapping("/cars")
    public ResponseEntity<List<Car>>allCars(){
        return ResponseEntity.ok(carService.getAvailableCars());
    }

    /*
    //SA
    @PostMapping("/addorder")//Skapa order (hyra bil)
    public ResponseEntity<String> addOrder(@RequestBody Booking booking){
        return ResponseEntity.created(service.addOrder(booking));
    }

    //SA
    @PutMapping("/cancelorder")//Avboka order
    public ResponseEntity<String> cancelOrder(@RequestParam Integer bookingId){
        return ResponseEntity.ok(service.cancelOrder(bookingId));
    }

    */
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
    //SA
    @PutMapping("/updateinfo")//Uppdatera sin information (dock ej personnumret)
    public ResponseEntity<Customer>updateInfo(@RequestBody Customer customer){
        return ResponseEntity.ok(service.updateInfo(customer));
    }*/
}
