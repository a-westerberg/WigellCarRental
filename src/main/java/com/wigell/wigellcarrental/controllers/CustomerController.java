package com.wigell.wigellcarrental.controllers;

import com.wigell.wigellcarrental.entities.Booking;
import com.wigell.wigellcarrental.entities.Car;
import com.wigell.wigellcarrental.entities.Customer;
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
    public ResponseEntity<String> addOrder(@RequestBody Booking booking){
        return ResponseEntity.created(service.addOrder(booking));
    }

    //TODO: cancel med id
    @PutMapping("/cancelorder")//Avboka order
    public ResponseEntity<String> cancelOrder(@RequestParam Integer bookingId){
        return ResponseEntity.ok(service.cancelOrder(bookingId));
    }

    @GetMapping("/activeorders")//Se aktiva bokningar
    public ResponseEntity<List<Booking>>activeOrders(){
        return ResponseEntity.ok(service.activeOrders());
    }

    @GetMapping("/orders")//Se tidigare bokningar
    public ResponseEntity<List<Booking>>orders(){
        return ResponseEntity.ok(service.orders());
    }

    @PutMapping("/updateinfo")//Uppdatera sin information (dock ej personnumret)
    public ResponseEntity<Customer>updateInfo(@RequestBody Customer customer){
        return ResponseEntity.ok(service.updateInfo(customer));
    }*/
}
