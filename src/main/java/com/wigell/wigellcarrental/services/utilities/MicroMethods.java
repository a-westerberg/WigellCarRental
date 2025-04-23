package com.wigell.wigellcarrental.services.utilities;

import com.wigell.wigellcarrental.exceptions.InvalidInputException;
// WIG-28-SJ
public class MicroMethods {

    // WIG-28-SJ & WIG-18-AA (instansof String)
    public static <T> T validateData(String resource, String field, T value) {
        if (value == null || (value instanceof String && ((String) value).isBlank())) {
            throw new InvalidInputException(resource, field, value);
        }
        return value;
    }


}
