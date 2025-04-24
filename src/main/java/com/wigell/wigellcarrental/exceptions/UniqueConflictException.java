package com.wigell.wigellcarrental.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// AWS / WIG 29-SJ
@ResponseStatus(HttpStatus.CONFLICT)
public class UniqueConflictException extends RuntimeException {

    public UniqueConflictException(String object, Object value) {
        super(object + ": {" + value + "} already exists, duplicates is not allowed.");
    }
}
