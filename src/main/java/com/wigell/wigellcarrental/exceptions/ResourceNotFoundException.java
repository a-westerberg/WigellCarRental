package com.wigell.wigellcarrental.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// WIG-10-SJ
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    private final String resource;
    private final String field;
    private final Object fieldValue;

    public ResourceNotFoundException(String resource, String field, Object fieldValue) {
        super(String.format("%s not found with %s: %s", resource, field, fieldValue));
        this.resource = resource;
        this.field = field;
        this.fieldValue = fieldValue;
    }

    public String getResource() {return resource;}
    public String getField() {return field;}
    public Object getFieldValue() {return fieldValue;}

}
