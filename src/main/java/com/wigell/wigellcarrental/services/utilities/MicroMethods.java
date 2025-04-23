package com.wigell.wigellcarrental.services.utilities;

import com.wigell.wigellcarrental.exceptions.InvalidInputException;
import com.wigell.wigellcarrental.exceptions.UniqueConflictException;

// WIG-28-SJ
public class MicroMethods {

    // WIG-28-SJ
    public static <T> void validateData(String resource, String field, T value) {
        if (value == null || (value instanceof String && ((String) value).isBlank())) {
            throw new InvalidInputException(resource, field, value);
        }
    }

    // WIG-29-SJ
    public static boolean validateForUpdate(Object value) {
        return value != null && (!(value instanceof String && ((String) value).isEmpty()));
    }

    // WIG-29-SJ
    // If values multiple values needs to be unique. If not, remove code later.
    public static <T> void validateUniqueValue(String fieldName, T value, java.util.function.Predicate<T> existsFunction) {
        if (value != null && existsFunction.test(value)) {
            throw new UniqueConflictException(fieldName, value);
        }
    }


}
