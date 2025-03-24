package com.ecomerce.sb_ecom.exceptions;

public class ResourceNotFoundException extends RuntimeException { // because when resource not found then it is runtime Exception
    // when you create custom exception typically it is runtimeException
    String resourceName;
    String field;
    String fieldName;
    Long fieldId;
    public ResourceNotFoundException(String resourceName, String field, String fieldName){
        super(String.format("%s not found with %s: %s", resourceName,field, fieldName));// call parent class constructor of runtimeException which is used to display the message
        this.resourceName = resourceName;
        this.field = field;
        this.fieldName = fieldName;
    }
    public ResourceNotFoundException(String resourceName, String field, Long fieldId){
        super(String.format("%s not found with %s: %s", resourceName,field, fieldId));
        this.resourceName = resourceName;
        this.field = field;
        this.fieldId = fieldId;
    }
    public  ResourceNotFoundException() {}

}
