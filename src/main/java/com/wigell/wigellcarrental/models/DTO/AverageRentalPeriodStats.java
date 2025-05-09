package com.wigell.wigellcarrental.models.DTO;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

//WIG-97-SJ
@JsonPropertyOrder({ "averageRentalPeriodInDays", "rentalDetails" })
public class AverageRentalPeriodStats {

    private double AverageRentalPeriodInDays;
    private List<RentalPeriodDetails> rentalDetails;

    public AverageRentalPeriodStats() {}
    public AverageRentalPeriodStats(double AverageRentalPeriodInDays, List<RentalPeriodDetails> rentalDetails) {
        this.AverageRentalPeriodInDays = AverageRentalPeriodInDays;
        this.rentalDetails = rentalDetails;
    }

    public double getAverageRentalPeriodInDays() {return AverageRentalPeriodInDays;}
    public void setAverageRentalPeriodInDays(double AverageRentalPeriodInDays) {this.AverageRentalPeriodInDays = AverageRentalPeriodInDays;}

    public List<RentalPeriodDetails> getRentalDetails() {return rentalDetails;}
    public void setRentalDetails(List<RentalPeriodDetails> rentalDetails) {this.rentalDetails = rentalDetails;}

    @Override
    public String toString() {
        return "AverageRentalPeriodStats{" +
                "AverageRentalPeriodInDays=" + AverageRentalPeriodInDays +
                ", rentalDetails=" + rentalDetails +
                '}';
    }
}
