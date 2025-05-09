package com.wigell.wigellcarrental.models.DTO;

import java.time.LocalDate;
import java.util.Map;

//WIG-96-AA
public class PopularBrandStatsDTO {

    private LocalDate startDate;
    private LocalDate endDate;
    Map<String,Long> brandCounts;

    public PopularBrandStatsDTO() {

    }

    public PopularBrandStatsDTO(LocalDate startDate, LocalDate endDate, Map<String,Long> brandCounts) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.brandCounts = brandCounts;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Map<String, Long> getBrandCounts() {
        return brandCounts;
    }

    public void setBrandCounts(Map<String, Long> brandCounts) {
        this.brandCounts = brandCounts;
    }

    @Override
    public String toString() {
        return "PopularBrandStatsDTO{" +
                "StartDate=" + startDate +
                ", EndDate=" + endDate +
                ", brandCounts=" + brandCounts +
                '}';
    }
}
