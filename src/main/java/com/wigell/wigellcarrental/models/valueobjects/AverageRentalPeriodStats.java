package com.wigell.wigellcarrental.models.valueobjects;

public class AverageRentalPeriodStats {

    private int AverageRentalPeriodInDays;

    public AverageRentalPeriodStats() {}
    public AverageRentalPeriodStats(int AverageRentalPeriodInDays) {
        this.AverageRentalPeriodInDays = AverageRentalPeriodInDays;
    }

    public int getAverageRentalPeriodInDays() {return AverageRentalPeriodInDays;}
    public void setAverageRentalPeriodInDays(int AverageRentalPeriodInDays) {this.AverageRentalPeriodInDays = AverageRentalPeriodInDays;}

    @Override
    public String toString() {
        return "AverageRentalPeriodStats{" +
                "AverageRentalPeriodInDays=" + AverageRentalPeriodInDays +
                '}';
    }
}
