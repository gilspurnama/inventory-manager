package com.example.inventory_management.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class ResponseException extends RuntimeException{
    private final Integer errorCode;
    private final String message;
    private final HttpStatus httpStatus;
}
