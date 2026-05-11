package com.online.shop.exception;

public class OrderProcessingException extends RuntimeException{
    public OrderProcessingException(String message){
        super(message);
    }
}
