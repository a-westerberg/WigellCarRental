package com.wigell.wigellcarrental.controllers;

import com.wigell.wigellcarrental.entities.Car;
import com.wigell.wigellcarrental.entities.Customer;
import com.wigell.wigellcarrental.entities.Order;
import com.wigell.wigellcarrental.services.OrderService;
import com.wigell.wigellcarrental.services.CarService;
import com.wigell.wigellcarrental.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

//SA
@RestController
@RequestMapping("/api/v1/admin")
//@PreAuthorize("ADMIN")
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
    /*
    //SA
    @GetMapping("/customer/{id}")//Lista specifik kund
    public ResponseEntity<Customer>getCustomer(@PathVariable String perNr){
        return ResponseEntity.ok(service.getCustomer(perNr));
    }
    //SA
    @PostMapping("/addcustomer")//Lägga till ny kund
    public ResponseEntity<String>addCustomer(@RequestBody Customer customer){
        return ResponseEntity.created(service.addCustomer(customer));
    }
    //SA
    @DeleteMapping("/removecustomer/{id}")//Radera befintlig kund
    public ResponseEntity<String>removeCustomer(@PathVariable String perNr){
        return ResponseEntity.ok(service.removeCustomer(perNr));
    }

     */
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
    /*
    //SA
    @PostMapping("/addcar")//Lägg till bil
    public ResponseEntity<String>addCar(@RequestBody Car car){
        return ResponseEntity.ok(service.addCar(car));
    }
    //SA
    @PutMapping("/updatecar")//Uppdatera bilinformation
    public ResponseEntity<Car>updateCar(@RequestBody Car car){
        return ResponseEntity.ok(service.updateCar(car));
    }
    //SA
    //TODO: PathVariable. RegNr eller id?
    @DeleteMapping("/removecar")//Radera bil
    public ResponseEntity<String>removeCar(@PathVariable String regNr){
        return ResponseEntity.ok(service.removeCar(car));
    }
    */
    //SA
    @GetMapping("/activeorders")//Lista alla aktiva ordrar
    public ResponseEntity<List<Order>>getAllActiveOrders(){
        return ResponseEntity.ok(orderService.getActiveOrders());
    }
/*
    //SA
    @GetMapping("/orders")//Lista historiska ordrar
    public ResponseEntity<List<Order>>getAllOrders(){
        return ResponseEntity.ok(service.getAllOrders());
    }
    //SA
    //TODO: PathVariable. Integer?
    @DeleteMapping("/removeorder")//Ta bort bokning från systemet
    public ResponseEntity<String>removeOrder(@PathVariable Integer bookingId){
        return ResponseEntity.ok(service.removeOrder(bookingId));
    }
    //SA
    //TODO: LocalDate
    @DeleteMapping("/removeorders-beforedate/{date}")
    public ResponseEntity<String>removeOrdersBeforeDate(@PathVariable LocalDate date){
        return ResponseEntity.ok(service.removeOrdersBeforeDate(date));
    }

    //SA
    @RequestMapping("/statistics")//String...  En oändlig array utan utsatt antal i, array oavsett om man skickar med en inparametrar
    public ResponseEntity<String> getStatistics(@RequestParam String choice, @RequestParam String... data){
        if(choice.contains("incomemonth")){//Total intäkt under en viss tidsperiod, månad och år
            return ResponseEntity.ok(service.getIncomeOnMoth(data[0],data[1]));

        } else if (choice.contains("brand")) {//mest hyrda bilmärke under en viss period, brand, datum1 och 2
            return ResponseEntity.ok(service.getPopularBrand(data[0],data[1],data[2]));

        } else if (choice.contains("car")) {//Antal gånger varje bil hyrts ut, bils-regNr
            return ResponseEntity.ok(service.getRented(data[0]));

        } else if (choice.contains("rentalperiod")) {//vanligaste hyresperiod (antal dagar)
            return ResponseEntity.ok(service.rentalPeriod());

        } else if (choice.contains("costperorder")) {//genomsnittlig kostnad per hyresorder
            return ResponseEntity.ok(service.costPerOrder());

        } else if (choice.contains("incomecar")) {//Total intäkt per bil, en eller flera bilar?
            return ResponseEntity.ok(service.incomeOnCar(data[0]));

        } else {
            return ResponseEntity.notFound(service.notfound());//Ok att göra?
        }
//egen package med ValueObjects utanför entities, en klass för varje
    }*/

}
