package com.wigell.wigellcarrental.controllers;

import com.wigell.wigellcarrental.entities.Car;
import com.wigell.wigellcarrental.entities.Customer;
import com.wigell.wigellcarrental.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
//@PreAuthorize("ADMIN")
public class AdminController {

    //Services
    //Car service, Customer service, Order service?
    private CustomerService customerService;

    @Autowired
    public AdminController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/customers")//Lista kunder
    public ResponseEntity<List<Customer>>customers(){
        return ResponseEntity.ok(customerService.getAllCustomers());
    }
    /*

    @GetMapping("/customer/{id}")//Lista specifik kund
    public ResponseEntity<Customer>getCustomer(@PathVariable String perNr){
        return ResponseEntity.ok(service.getCustomer(perNr));
    }

    @PostMapping("/addcustomer")//Lägga till ny kund
    public ResponseEntity<String>addCustomer(@RequestBody Customer customer){
        return ResponseEntity.created(service.addCustomer(customer));
    }

    @DeleteMapping("/removecustomer/{id}")//Radera befintlig kund
    public ResponseEntity<String>removeCustomer(@PathVariable String perNr){
        return ResponseEntity.ok(service.removeCustomer(perNr));
    }

    @GetMapping("/cars")//Lista tillgängliga bilar
    public ResponseEntity<List<Car>>getAllCars(){
        return ResponseEntity.ok(service.getAllAvailableCars());
    }

    @GetMapping("/allcars")//Lista samtliga bilar
    public ResponseEntity<List<Car>>getAllAvailableCars(){
        return ResponseEntity.ok(service.getAllAvailableCars());
    }

    @PostMapping("/addcar")//Lägg till bil
    public ResponseEntity<String>addCar(@RequestBody Car car){
        return ResponseEntity.ok(service.addCar(car));
    }

    @PutMapping("/updatecar")//Uppdatera bilinformation
    public ResponseEntity<Car>updateCar(@RequestBody Car car){
        return ResponseEntity.ok(service.updateCar(car));
    }

    //TODO: PathVariable. RegNr eller id?
    @DeleteMapping("/removecar")//Radera bil
    public ResponseEntity<String>removeCar(@PathVariable String regNr){
        return ResponseEntity.ok(service.removeCar(car));
    }

    @GetMapping("/activeorders")//Lista alla aktiva ordrar
    public ResponseEntity<List<Order>>getAllActiveOrders(){
        return ResponseEntity.ok(service.getActiveOrders());
    }

    @GetMapping("/orders")//Lista historiska ordrar
    public ResponseEntity<List<Order>>getAllOrders(){
        return ResponseEntity.ok(service.getAllOrders());
    }

    //TODO: PathVariable. Integer?
    @DeleteMapping("/removeorder")//Ta bort bokning från systemet
    public ResponseEntity<String>removeOrder(@PathVariable Integer bookingId){
        return ResponseEntity.ok(service.removeOrder(bookingId));
    }

    //TODO: LocalDate
    @DeleteMapping("/removeorders-beforedate/{date}")
    public ResponseEntity<String>removeOrdersBeforeDate(@PathVariable LocalDate date){
        return ResponseEntity.ok(service.removeOrdersBeforeDate(date));
    }


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
