package com.wigell.wigellcarrental.models.valueobjects;

import java.math.BigDecimal;


// WIG-114-AWS
public class MonthlyIncomeStats {
    private final int month;
    private final int year;
    private final BigDecimal totalIncome;


    public MonthlyIncomeStats(int month, int year, BigDecimal totalIncome) {
        this.month = month;
        this.year = year;
        this.totalIncome = totalIncome;
    }

    public int getMonth() {
        return month;
    }
    public int getYear() {
        return year;
    }
    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

}
