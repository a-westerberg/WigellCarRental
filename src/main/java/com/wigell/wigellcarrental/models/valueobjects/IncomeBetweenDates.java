package com.wigell.wigellcarrental.models.valueobjects;

import java.math.BigDecimal;
import java.time.LocalDate;
    //WIG-114-AWS
public class IncomeBetweenDates {
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final BigDecimal totalIncome;

    public IncomeBetweenDates(LocalDate startDate, LocalDate endDate, BigDecimal totalIncome) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalIncome = totalIncome;
    }

        public LocalDate getStartDate() {
            return startDate;
        }

        public LocalDate getEndDate() {
            return endDate;
        }

        public BigDecimal getTotalIncome() {
            return totalIncome;
        }
    }
