package com.wigell.wigellcarrental.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// AWS
@ResponseStatus(HttpStatus.CONFLICT)
public class UniqueConflictException extends RuntimeException {

    public UniqueConflictException(String object, String value) {
        super(object + ": {" + value + "} already exists, duplicates is not allowed.");
    }
}
