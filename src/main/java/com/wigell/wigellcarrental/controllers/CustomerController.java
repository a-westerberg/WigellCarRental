package com.wigell.wigellcarrental.controllers;

import com.wigell.wigellcarrental.entities.Booking;
import com.wigell.wigellcarrental.entities.Car;
import com.wigell.wigellcarrental.entities.Customer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//SA
@RestController
@RequestMapping("/api/v1")
//@PreAuthorize("USER")
public class CustomerController {

    //Services
    //SA
    /*@GetMapping("/cars")//Lista tillgängliga bilar
    public ResponseEntity<List<Car>>allCars(){
        return ResponseEntity.ok(service.getAllAvailableCars());
    }
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
    //SA
    @GetMapping("/activeorders")//Se aktiva bokningar
    public ResponseEntity<List<Booking>>activeOrders(){
        return ResponseEntity.ok(service.activeOrders());
    }
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
