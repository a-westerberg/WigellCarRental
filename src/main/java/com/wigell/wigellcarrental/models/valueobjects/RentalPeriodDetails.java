package com.wigell.wigellcarrental.models.valueobjects;

import java.time.LocalDate;

//WIG-97-SJ
public class RentalPeriodDetails {

    private final long orderId;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final long numberOfDays;

    public RentalPeriodDetails(long orderId,LocalDate startDate, LocalDate endDate, long numberOfDays) {
        this.orderId = orderId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.numberOfDays = numberOfDays;
    }

    public long getOrderId() {return orderId;}
    public LocalDate getStartDate() {return startDate;}
    public LocalDate getEndDate() {return endDate;}
    public long getNumberOfDays() {return numberOfDays;}

    @Override
    public String toString() {
        return "RentalPeriodDetails{" +
                "startDate=" + startDate +
                ", endDate=" + endDate +
                ", numberOfDays=" + numberOfDays +
                '}';
    }
}
