package com.online.shop.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import com.online.shop.dtos.ErrorResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler{

        //Validation Error Handling
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,HttpHeaders headers,HttpStatusCode statusCode,WebRequest request){
        Map<String,String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(),error.getDefaultMessage())
        );

        ErrorResponse errorResponse = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Validation Failed!",
            "ဖြည့်စွက်ထားသောအချက်အလက်မှားယွင်းနေပါတယ်",
            request.getDescription(false),
            errors
        );
        return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
    }

    //Database Integrity Error
    @ExceptionHandler(DataIntegrityViolationException.class)
        public ResponseEntity<ErrorResponse> handleDateIntegrity(DataIntegrityViolationException ex,WebRequest request){
            ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                "Database Error",
                "ဤအချက်အလက်သည် စနစ်ထဲတွင် ရှိနှင့်ပြီးသားဖြစ်သည် သို့မဟုတ် ချိတ်ဆက်မှု မှားယွင်းနေသည်",
                request.getDescription(false)
            );
            return new ResponseEntity<>(error,HttpStatus.CONFLICT);
        }

    //Not Found Error
    @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex,WebRequest request){
            ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getDescription(false)
            );
            return new ResponseEntity<>(error,HttpStatus.NOT_FOUND);
    }

    //Custom Business Exception(Order Processing)
    @ExceptionHandler(OrderProcessingException.class)
        public ResponseEntity<ErrorResponse> handleOrderError(OrderProcessingException ex,WebRequest request){
            ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Order Error",
                ex.getMessage(),
                request.getDescription(false)
            );
            return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
    }

    //Generic Exception (FallBack)
    @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex,WebRequest request){
            log.error("An excepted error occured!",ex);
            ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Srver Error",
                "Something went wrong,Try again later",
                request.getDescription(false)
            );
            return new ResponseEntity<>(error,HttpStatus.INTERNAL_SERVER_ERROR);
    }
}


