package com.wigell.wigellcarrental.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wigell.wigellcarrental.enums.CarStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

//WIG-5-AA
@Entity
@Table(name = "cars")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "make", nullable = false, length = 20)
    private String make;

    @Column(name = "model", nullable = false, length = 20)
    private String model;

    @Column(name = "registration_number", nullable = false, length = 6)
    private String registrationNumber;

    //WIG-7-AA
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CarStatus status;

    @Column(name = "pricePerDay")
    private BigDecimal pricePerDay;

   @OneToMany(mappedBy = "car")
   private List<Order> orders;

   //SA
    public Car(Long id) {
        this.id = id;
    }

    //SA
    public Car(Long id, String make, String model, String registrationNumber, CarStatus status, BigDecimal pricePerDay, List<Order> orders) {
        this.id = id;
        this.make = make;
        this.model = model;
        this.registrationNumber = registrationNumber;
        this.status = status;
        this.pricePerDay = pricePerDay;
        this.orders = orders;
    }

    public Car() {
        orders = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public CarStatus getStatus() {
        return status;
    }

    public void setStatus(CarStatus status) {
        this.status = status;
    }

    public BigDecimal getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(BigDecimal pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    @JsonIgnore
    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    @Override
    public String toString() {
        return "Car{" +
                "id=" + id +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", registrationNumber='" + registrationNumber + '\'' +
                ", status=" + status +
                ", pricePerDay=" + pricePerDay +
                '}';
    }
}
