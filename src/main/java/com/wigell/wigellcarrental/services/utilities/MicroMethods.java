package com.wigell.wigellcarrental.services.utilities;

import com.wigell.wigellcarrental.exceptions.InvalidInputException;
// WIG-28-SJ
public class MicroMethods {

    // WIG-28-SJ
    public static <T> T validateData(String resource, String field, T value) {
        if (value == null) {
            throw new InvalidInputException(resource, field, value);
        }
        return value;
    }


}
