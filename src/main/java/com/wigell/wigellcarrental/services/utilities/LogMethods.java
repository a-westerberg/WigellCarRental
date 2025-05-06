package com.wigell.wigellcarrental.services.utilities;

import java.lang.reflect.Field;
import java.util.Map;

//WIG-68-AWS, Log output design av SA
public class LogMethods {

    //WIG-24-AWS
    public static <T> String logUpdateBuilder(T oldObject, T newObject, String... fieldsToCompare) {
        StringBuilder log = new StringBuilder();

        for(String fieldName : fieldsToCompare) {
            try{
                Field field = oldObject.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);

                Object oldValue = field.get(oldObject);
                Object newValue = field.get(newObject);

                if(oldValue != null && !oldValue.equals(newValue) || (oldValue == null && newValue != null)) {
                    log.append(String.format("\n\t%s: '%s' -> '%s'; ", fieldName, oldValue, newValue));
                }
            } catch (NoSuchFieldException | IllegalAccessException e){
                log.append(String.format("\n\t%s: [error reading field]; ", fieldName));
            }
        }
        return log.toString();
    }

    //WIG-68-AWS-Part2
    public static String logUpdateBuilder(Map<String, Object> oldValues, Map<String, Object> newValues) {
        StringBuilder log = new StringBuilder();

        for(String key : oldValues.keySet()) {
            Object newValue = newValues.get(key);
            Object oldValue = oldValues.get(key);

            if(oldValue != null && !oldValue.equals(newValue) || (oldValue == null && newValue != null)) {
                log.append(String.format("\n\t%s: '%s' -> '%s'; ", key, oldValue, newValue));
            }
        }
        return log.toString();
    }

    //WIG-68-AWS
    public static <T> String logBuilder(T object, String... fieldsToLog){
        StringBuilder log = new StringBuilder();

        for(String fieldName : fieldsToLog) {
            try{
                Field field = object.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                Object value = field.get(object);
                log.append(String.format("\n\t%s: '%s'", fieldName, value));
            } catch (NoSuchFieldException | IllegalAccessException e){
                log.append(String.format("\n\t%s: [error reading field]; ", fieldName));
            }
        }
        return log.toString();
    }

    //WIG-68-AWS-Part2
    public static String logBuilder(Map<String, Object> fields){
        StringBuilder log = new StringBuilder();

        for(Map.Entry<String, Object> entry : fields.entrySet()) {
            log.append(String.format("\n\t%s: '%s'", entry.getKey(), entry.getValue()));
        }
        return log.toString();
    }

    //WIG-68-AWS
    public static <T> String logExceptionBuilder(T object, Exception exception, String... fieldsToLog){
        StringBuilder log = new StringBuilder();

        log.append("\nException: ").append(exception.getClass().getSimpleName());
        log.append(" - ").append(exception.getMessage());
        log.append("\nAttempted values:");

        for(String fieldName : fieldsToLog) {
            try{
                Field field = object.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                Object value = field.get(object);
                log.append(String.format("\n\t%s: '%s'", fieldName, value));
            } catch (NoSuchFieldException | IllegalAccessException e){
                log.append(String.format("\n\t%s: [error reading field] ;", fieldName));
            }
        }
        return log.toString();
    }

    //WIG-68-AWS-Part2
    public static String logExceptionBuilder(Map<String, Object> attemptedValues, Exception exception){
        StringBuilder log = new StringBuilder();

        log.append("\nException: ").append(exception.getClass().getSimpleName());
        log.append(" - ").append(exception.getMessage());
        log.append("\nAttempted values:");

        attemptedValues.forEach((key, value) ->
                log.append(String.format("\n\t%s: '%s'", key, value))
        );
        return log.toString();
    }
}
