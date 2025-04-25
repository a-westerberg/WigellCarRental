package com.wigell.wigellcarrental.services.utilities;

import com.wigell.wigellcarrental.entities.Order;
import com.wigell.wigellcarrental.exceptions.InvalidInputException;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;

import com.wigell.wigellcarrental.exceptions.UniqueConflictException;

import java.util.List;
import java.util.function.Consumer;

// WIG-28-SJ
public class MicroMethods {

    // WIG-28-SJ & WIG-18-AA (instansof String)
    public static <T> void validateData(String resource, String field, T value) {
        if (value == null || (value instanceof String && ((String) value).isBlank())) {
            throw new InvalidInputException(resource, field, value);
        }
    }

    // WIG-29-SJ
    public static boolean validateNotNull (Object value) {
        return value != null && (!(value instanceof String && ((String) value).isEmpty()));
    }

    // WIG-29-SJ
    // If values multiple values needs to be unique. If not, remove code later.
    public static <T> void validateUniqueValue(String fieldName, T value, java.util.function.Predicate<T> existsFunction) {
        if (value != null && existsFunction.test(value)) {
            throw new UniqueConflictException(fieldName, value);
        }
    }

    //SA
    public static BigDecimal calculateCancellationFee(Order orderToCancel){
        long days = ChronoUnit.DAYS.between(orderToCancel.getStartDate(), orderToCancel.getEndDate());
        return orderToCancel.getTotalPrice().multiply(BigDecimal.valueOf(0.05).multiply(BigDecimal.valueOf(days)));
    }

    // WIG-30-SJ
    public static <T> void disconnectKeys (List<T> entities, Consumer<T> disconnectAction, Consumer<T> saveAction) {
        for (T entity : entities) {
            disconnectAction.accept(entity);
            saveAction.accept(entity);
        }
    }


}
