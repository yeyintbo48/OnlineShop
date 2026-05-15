package com.online.shop.dtos;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
    LocalDateTime timeStamp,
    int status,
    String error,
    String message,
    String path,
    Map<String,String> validationErrors
) {
    public ErrorResponse(LocalDateTime timeStamp,int status,String error,String message,String path){
        this(timeStamp,status,error,message,path,null);
    }
}
