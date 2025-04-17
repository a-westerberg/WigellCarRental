package com.wigell.wigellcarrental.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
//@PreAuthorize("USER")
public class CustomerController {

    //Services

    /*@GetMapping("/cars")//Lista tillgängliga bilar
    public ResponseEntity<List<Car>>allCars(){
        return ResponseEntity.ok(service.getAllAvailableCars());
    }

    @PostMapping("/addorder")//Skapa order (hyra bil)
    public ResponseEntity<String> addOrder(@RequestBody Order order){
        return new ResponseEntity<>(service.addOrder(order), HttpStatus.CREATED);
    }

    //TODO: cancel med id
    @PutMapping("/cancelorder")//Avboka order
    public ResponseEntity<String> cancelOrder(@RequestParam Integer orderId){
        return ResponseEntity.ok(service.cancelOrder(orderId));
    }

    @GetMapping("/activeorders")//Se aktiva bokningar
    public ResponseEntity<List<Order>>activeOrders(){
        return ResponseEntity.ok(service.activeOrders());
    }

    @GetMapping("/orders")//Se tidigare bokningar
    public ResponseEntity<List<Order>>orders(){
        return ResponseEntity.ok(service.orders());
    }

    @PutMapping("/updateinfo")//Uppdatera sin information (dock ej personnumret)
    public ResponseEntity<Customer>updateInfo(@RequestBody Customer customer){
        return ResponseEntity.ok(service.updateInfo(customer));
    }*/
}
