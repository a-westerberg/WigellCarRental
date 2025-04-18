package com.wigell.wigellcarrental.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;

//WIG-5-AA
@Entity
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "make", nullable = false, length = 20)
    private String make;

    @Column(name = "model", nullable = false, length = 20)
    private String model;

    @Column(name = "registration_number", nullable = false, length = 7)
    private String registrationNumber;

/*    @Column(name = "status", nullable = false)
    private CarStatus status;*/

    @Column(name = "pricePerDay")
    private BigDecimal pricePerDay;

    public Car() {

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

/*    public CarStatus getStatus() {
        return status;
    }

    public void setStatus(CarStatus status) {
        this.status = status;
    }*/

    public BigDecimal getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(BigDecimal pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    @Override
    public String toString() {
        return "Car{" +
                "id=" + id +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", registrationNumber='" + registrationNumber + '\'' +
               /* ", status=" + status +*/
                ", pricePerDay=" + pricePerDay +
                '}';
    }
}
