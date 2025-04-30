package com.wigell.wigellcarrental.services.utilities;

import com.wigell.wigellcarrental.exceptions.InvalidInputException;

import java.time.LocalDate;

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

    // WIG-30-SJ
    public static <T> void disconnectKeys (List<T> entities, Consumer<T> disconnectAction, Consumer<T> saveAction) {
        for (T entity : entities) {
            disconnectAction.accept(entity);
            saveAction.accept(entity);
        }
    }

    //WIG-85-AA
    public static LocalDate parseStringToDate(String date) {
        try {
            return LocalDate.parse(date);
        } catch (Exception e) {
            throw new InvalidInputException("statistics","data", date);
        }
    }


}
