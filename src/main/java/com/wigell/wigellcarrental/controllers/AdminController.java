package com.wigell.wigellcarrental.controllers;

import com.wigell.wigellcarrental.entities.Car;
import com.wigell.wigellcarrental.entities.Customer;
import com.wigell.wigellcarrental.entities.Order;
import com.wigell.wigellcarrental.services.OrderService;
import com.wigell.wigellcarrental.services.CarService;
import com.wigell.wigellcarrental.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

//SA
@RestController
@RequestMapping("/api/v1/admin")
//@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    //Services
    //Car service, Customer service, Order service?
    private final CustomerService customerService;
    private final CarService carService;            //aws
    private OrderService orderService;

    @Autowired
    public AdminController(OrderService orderService, CustomerService customerService,CarService carService) {
        this.orderService = orderService;
        this.customerService = customerService;
        this.carService = carService;
    }


    //SA
    @GetMapping("/customers")//Lista kunder
    public ResponseEntity<List<Customer>>customers(){
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    //SA / WIG-27-SJ
    @GetMapping("/customer/{id}")
    public ResponseEntity<Customer>getCustomer(@PathVariable Long id){
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    /*
    //SA
    @PostMapping("/addcustomer")//Lägga till ny kund
    public ResponseEntity<String>addCustomer(@RequestBody Customer customer){
        return ResponseEntity.created(service.addCustomer(customer));
    }
    */


    //SA / WIG-30-SJ
    @DeleteMapping("/removecustomer/{id}")
    public ResponseEntity<String>removeCustomer(@PathVariable Long id){
        return ResponseEntity.ok(customerService.removeCustomerById(id));
    }


    //SA // AWS
    @GetMapping("/cars")//Lista tillgängliga bilar
    public ResponseEntity<List<Car>>getAllAvailableCars(){
        List<Car> availableCars = carService.getAvailableCars();
        return ResponseEntity.ok(availableCars);
    }

    //SA //AA
    @GetMapping("/allcars")//Lista samtliga bilar
    public ResponseEntity<List<Car>>getAllCars(){
        return ResponseEntity.ok(carService.getAllCars());
    }

    //SA //AA
    @PostMapping("/addcar")//Lägg till bil
    public ResponseEntity<Car>addCar(@RequestBody Car car){
        return ResponseEntity.ok(carService.addCar(car));
    }
    /*
    //SA
    @PutMapping("/updatecar")//Uppdatera bilinformation
    public ResponseEntity<Car>updateCar(@RequestBody Car car){
        return ResponseEntity.ok(service.updateCar(car));
    }
    */
    //SA //AA
    @DeleteMapping("/removecar/{idOrRegistrationNumber}")//Radera bil
    public ResponseEntity<String>removeCar(@PathVariable String idOrRegistrationNumber){
        return ResponseEntity.ok(carService.deleteCar(idOrRegistrationNumber));
    }

    //SA
    @GetMapping("/activeorders")//Lista alla aktiva ordrar
    public ResponseEntity<List<Order>>getAllActiveOrders(){
        return ResponseEntity.ok(orderService.getActiveOrders());
    }

    //SA
    @GetMapping("/orders")//Lista historiska ordrar
    public ResponseEntity<List<Order>>getAllOrders(){
        return ResponseEntity.ok(orderService.getAllOrdersHistory());
    }
    /*
    //SA
    //TODO: PathVariable. Integer?
    @DeleteMapping("/removeorder")//Ta bort bokning från systemet
    public ResponseEntity<String>removeOrder(@PathVariable Integer bookingId){
        return ResponseEntity.ok(service.removeOrder(bookingId));
    }
    */

    //SA
    //TODO: LocalDate
    @DeleteMapping("/removeorders-beforedate/{date}")
    public ResponseEntity<String>removeOrdersBeforeDate(@PathVariable LocalDate date, Principal principal){
        return ResponseEntity.ok(orderService.removeOrdersBeforeDate(date,principal));
    }

    //SA
    @PutMapping("/updateorderstatus/{orderId}/{status}")
    public ResponseEntity<String>updateOrderStatus(@PathVariable Long orderId, @PathVariable String status,Principal principal){
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId,status,principal));
    }

    //SA
    @PutMapping("/updateordercar/{orderId}/{carId}")
    public ResponseEntity<String>updateOrderCar(@PathVariable Long orderId, @PathVariable Long carId,Principal principal){
        return ResponseEntity.ok(orderService.updateOrderCar(orderId,carId,principal));
    }

    //SA
    @RequestMapping("/statistics")//String...  En oändlig array utan utsatt antal i, array oavsett om man skickar med en inparametrar
    public ResponseEntity<String> getStatistics(@RequestParam String choice, @RequestParam String... data){
        /*if(choice.contains("incomemonth")){//Total intäkt under en viss tidsperiod, månad och år
            return ResponseEntity.ok(service.getIncomeOnMoth(data[0],data[1]));

        } else if (choice.contains("brand")) {//mest hyrda bilmärke under en viss period, brand, datum1 och 2
            return ResponseEntity.ok(service.getPopularBrand(data[0],data[1],data[2]));

        } else if (choice.contains("rentalperiod")) {//vanligaste hyresperiod (antal dagar)
            return ResponseEntity.ok(service.rentalPeriod());

        } else if (choice.contains("costperorder")) {//genomsnittlig kostnad per hyresorder
            return ResponseEntity.ok(service.costPerOrder());

        } else*/
        if (choice.contains("incomecar")) {//Total intäkt per bil och hur många gånger de hyrts ut
            return ResponseEntity.ok(carService.incomeOnCars());

        } /*else {
            return ResponseEntity.notFound(service.notfound());//Ok att göra?
        }*/
        return ResponseEntity.ok("Mop");
//egen package med ValueObjects utanför entities, en klass för varje
    }

}
