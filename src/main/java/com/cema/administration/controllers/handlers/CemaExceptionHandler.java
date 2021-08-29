package com.cema.administration.controllers.handlers;

import com.cema.administration.domain.ErrorResponse;
import com.cema.administration.exceptions.EstablishmentAlreadyExistsException;
import com.cema.administration.exceptions.EstablishmentNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CemaExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EstablishmentAlreadyExistsException.class)
    public final ResponseEntity<Object> handleEstablishmentAlreadyExistsException(EstablishmentAlreadyExistsException ex, WebRequest request) {

        ErrorResponse error = new ErrorResponse("Establishment Already Exists", ex.getMessage());
        return new ResponseEntity(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EstablishmentNotFoundException.class)
    public final ResponseEntity<Object> handleEstablishmentNotFoundException(EstablishmentNotFoundException ex, WebRequest request) {

        ErrorResponse error = new ErrorResponse("Establishment Not Found", ex.getMessage());
        return new ResponseEntity(error, HttpStatus.NOT_FOUND);
    }
}
