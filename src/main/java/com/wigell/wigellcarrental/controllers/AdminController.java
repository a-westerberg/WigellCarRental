package com.wigell.wigellcarrental.controllers;

import com.wigell.wigellcarrental.models.entities.Car;
import com.wigell.wigellcarrental.models.entities.Customer;
import com.wigell.wigellcarrental.models.entities.Order;
import com.wigell.wigellcarrental.services.OrderService;
import com.wigell.wigellcarrental.services.CarService;
import com.wigell.wigellcarrental.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

//SA
@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    //Services
    //Car service, Customer service, Order service?
    private final CustomerService customerService;
    private final CarService carService;            //aws
    private final OrderService orderService; //AA added final

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


    //SA // WIG-23-AWS
    @PostMapping("/addcustomer")//Lägga till ny kund
    public ResponseEntity<Customer>addCustomer(@RequestBody Customer customer, Principal principal){
        return new ResponseEntity<>(customerService.addCustomer(customer, principal), HttpStatus.CREATED);
    }



    //SA / WIG-30-SJ
    @DeleteMapping("/removecustomer/{id}")
    public ResponseEntity<String>removeCustomer(@PathVariable Long id, Principal principal){
        return ResponseEntity.ok(customerService.removeCustomerById(id, principal));
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
    public ResponseEntity<Car>addCar(@RequestBody Car car, Principal principal){
        return new ResponseEntity<>(carService.addCar(car,principal), HttpStatus.CREATED);
    }

    //SA //AWS
    @PutMapping("/updatecar")//Uppdatera bilinformation
    public ResponseEntity<Car>updateCar(@RequestBody Car car, Principal principal){
        return ResponseEntity.ok(carService.updateCar(car, principal));
    }

    //SA //AA
    @DeleteMapping("/removecar/{idOrRegistrationNumber}")
    public ResponseEntity<String>removeCar(@PathVariable String idOrRegistrationNumber, Principal principal){
        return ResponseEntity.ok(carService.deleteCar(idOrRegistrationNumber, principal));
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

    //SA //AWS
    //TODO: PathVariable. Integer?
    @DeleteMapping("/removeorder/{orderId}")//Ta bort bokning från systemet
    public ResponseEntity<String>removeOrder(@PathVariable Long orderId, Principal principal){
        return ResponseEntity.ok(orderService.removeOrderById(orderId, principal));
    }

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
    public ResponseEntity<?> getStatistics(@RequestParam String choice, @RequestParam String... data){
        // WIG-114-AWS
        if(choice.contains("incomemonth")){ //Total intäkt under en viss tidsperiod, månad och år
            return ResponseEntity.ok(orderService.getIncomeOnMonth(data[0],data[1]));
        }
        else if (choice.contains("incomebetweendates")) {
            return ResponseEntity.ok(orderService.getIncomeBetweenDates(data[0],data[1]));
        }
        else if (choice.contains("incomeyear")) {
            return ResponseEntity.ok(orderService.getIncomeByYear(data[0]));
        }
        else if (choice.contains("brand")) {//mest hyrda bilmärke under en viss period, brand, datum1 och 2
            return ResponseEntity.ok(orderService.getPopularBrand(data[0],data[1]));

        }
        /*
        else if (choice.contains("car")) {//Antal gånger varje bil hyrts ut, bils-regNr
            return ResponseEntity.ok(service.getRented(data[0])); }
        */

        //WIG-97-SJ
        else if (choice.contains("rentalperiod")) {
            return ResponseEntity.ok(orderService.getAverageRentalPeriod());

        }
        //WIG-97-SJ
        else if (choice.contains("costperorder")) {
            return ResponseEntity.ok(orderService.costPerOrder());

        }


        else if (choice.contains("incomecar")) {//Total intäkt per bil och hur många gånger de hyrts ut
            return ResponseEntity.ok(carService.incomeOnCars());

        } /*else {
            return ResponseEntity.notFound(service.notfound());//Ok att göra?
        }*/
        return ResponseEntity.badRequest().body("Ta bort senare/Choice for '" + choice + "' not found");
//egen package med ValueObjects utanför entities, en klass för varje
    }

}
