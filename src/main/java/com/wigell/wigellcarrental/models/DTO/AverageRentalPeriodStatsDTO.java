package com.wigell.wigellcarrental.models.DTO;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

//WIG-97-SJ
@JsonPropertyOrder({ "averageRentalPeriodInDays", "rentalDetails" })
public class AverageRentalPeriodStatsDTO {

    private double AverageRentalPeriodInDays;
    private List<RentalPeriodDetailsDTO> rentalDetails;

    public AverageRentalPeriodStatsDTO() {}
    public AverageRentalPeriodStatsDTO(double AverageRentalPeriodInDays, List<RentalPeriodDetailsDTO> rentalDetails) {
        this.AverageRentalPeriodInDays = AverageRentalPeriodInDays;
        this.rentalDetails = rentalDetails;
    }

    public double getAverageRentalPeriodInDays() {return AverageRentalPeriodInDays;}
    public void setAverageRentalPeriodInDays(double AverageRentalPeriodInDays) {this.AverageRentalPeriodInDays = AverageRentalPeriodInDays;}

    public List<RentalPeriodDetailsDTO> getRentalDetails() {return rentalDetails;}
    public void setRentalDetails(List<RentalPeriodDetailsDTO> rentalDetails) {this.rentalDetails = rentalDetails;}

    @Override
    public String toString() {
        return "AverageRentalPeriodStats{" +
                "AverageRentalPeriodInDays=" + AverageRentalPeriodInDays +
                ", rentalDetails=" + rentalDetails +
                '}';
    }
}
