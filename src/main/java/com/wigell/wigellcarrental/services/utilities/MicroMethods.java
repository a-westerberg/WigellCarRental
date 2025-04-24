package com.wigell.wigellcarrental.services.utilities;

import com.wigell.wigellcarrental.entities.Order;
import com.wigell.wigellcarrental.exceptions.InvalidInputException;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;

// WIG-28-SJ
public class MicroMethods {

    // WIG-28-SJ & WIG-18-AA (instansof String)
    public static <T> T validateData(String resource, String field, T value) {
        if (value == null || (value instanceof String && ((String) value).isBlank())) {
            throw new InvalidInputException(resource, field, value);
        }
        return value;
    }

    //SA
    public static BigDecimal calculateCancellationFee(Order orderToCancel){
        long days = ChronoUnit.DAYS.between(orderToCancel.getStartDate(), orderToCancel.getEndDate());
        //BigDecimal fivePercent = orderToCancel.getTotalPrice().multiply(BigDecimal.valueOf(0.05));

        //BigDecimal cancellationFee = orderToCancel.getTotalPrice().multiply(BigDecimal.valueOf(0.05).multiply(BigDecimal.valueOf(days)));
        return orderToCancel.getTotalPrice().multiply(BigDecimal.valueOf(0.05).multiply(BigDecimal.valueOf(days)));
    }


}
