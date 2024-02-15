package com.ecommerce.common.exception;

public class OrderNotFoundException extends Exception {
    public OrderNotFoundException(String message) {
    	super(message);
    }
}
