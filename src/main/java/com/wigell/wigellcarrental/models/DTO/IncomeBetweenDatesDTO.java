package com.wigell.wigellcarrental.models.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;
    //WIG-114-AWS
public class IncomeBetweenDatesDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalIncome;


        public IncomeBetweenDatesDTO() {
        }

        public IncomeBetweenDatesDTO(LocalDate startDate, LocalDate endDate, BigDecimal totalIncome) {
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

        public void setTotalIncome(BigDecimal totalIncome){
            this.totalIncome = totalIncome;
        }


        @Override
        public String toString() {
            return "IncomeBetweenDatesDTO{" +
                    "startDate=" + startDate +
                    ", endDate=" + endDate +
                    ", totalIncome=" + totalIncome +
                    '}';
        }
    }
