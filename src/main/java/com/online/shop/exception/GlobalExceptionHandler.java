package com.online.shop.exception;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import com.online.shop.dtos.ErrorResponse;

@ControllerAdvice
public class GlobalExceptionHandler{
    @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex,WebRequest request){
            ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getDescription(false));
                return new ResponseEntity<>(error,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(OrderProcessingException.class)
        public ResponseEntity<ErrorResponse> handleOrderError(OrderProcessingException ex,WebRequest request){
            ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Order Error",
                ex.getMessage(),
                request.getDescription(false));
                return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
    }

    //Generic Exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex,WebRequest request){
        ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Srver Error",
            "Something went wrong,Try again later",
            request.getDescription(false));
            return new ResponseEntity<>(error,HttpStatus.INTERNAL_SERVER_ERROR);
    }
}


