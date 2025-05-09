package com.wigell.wigellcarrental.models.DTO;

import java.time.LocalDate;

//WIG-97-SJ
public class RentalPeriodDetailsDTO {

    private long orderId;
    private LocalDate startDate;
    private LocalDate endDate;
    private long numberOfDays;

    public RentalPeriodDetailsDTO() {}
    public RentalPeriodDetailsDTO(long orderId, LocalDate startDate, LocalDate endDate, long numberOfDays) {
        this.orderId = orderId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.numberOfDays = numberOfDays;
    }

    public long getOrderId() {return orderId;}
    public void setOrderId(long orderId) {this.orderId = orderId;}

    public LocalDate getStartDate() {return startDate;}
    public void setStartDate(LocalDate startDate) {this.startDate = startDate;}

    public LocalDate getEndDate() {return endDate;}
    public void setEndDate(LocalDate endDate) {this.endDate = endDate;}

    public long getNumberOfDays() {return numberOfDays;}
    public void setNumberOfDays(long numberOfDays) {this.numberOfDays = numberOfDays;}

    @Override
    public String toString() {
        return "RentalPeriodDetailsDTO{" +
                "orderId=" + orderId +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", numberOfDays=" + numberOfDays +
                '}';
    }
}
