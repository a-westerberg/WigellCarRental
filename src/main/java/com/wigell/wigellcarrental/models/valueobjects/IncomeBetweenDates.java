package com.wigell.wigellcarrental.models.valueobjects;

import java.math.BigDecimal;
import java.time.LocalDate;

public class IncomeBetweenDates {
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalIncome;

    public IncomeBetweenDates(LocalDate startDate, LocalDate endDate, BigDecimal totalIncome) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalIncome = totalIncome;
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

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }
}
