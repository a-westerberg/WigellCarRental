package com.wigell.wigellcarrental.models.DTO;

import com.wigell.wigellcarrental.models.entities.Car;

import java.math.BigDecimal;

//SA
public class IncomeCarDTO {
    private Car car;
    private int rentedTimes;
    private BigDecimal totalIncome;

    public IncomeCarDTO() {
    }

    public IncomeCarDTO(Car car, int rentedTimes, BigDecimal totalIncome) {
        this.car = car;
        this.rentedTimes = rentedTimes;
        this.totalIncome = totalIncome;
    }

    @Override
    public String toString() {
        return "IncomeCar{" +
                "car=" + car +
                ", rentedTimes=" + rentedTimes +
                ", totalIncome=" + totalIncome +
                '}';
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public int getRentedTimes() {
        return rentedTimes;
    }

    public void setRentedTimes(int rentedTimes) {
        this.rentedTimes = rentedTimes;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }
}
