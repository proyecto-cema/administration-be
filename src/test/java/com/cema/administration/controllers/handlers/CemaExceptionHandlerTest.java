package com.cema.administration.controllers.handlers;

import com.cema.administration.domain.ErrorResponse;
import com.cema.administration.exceptions.EstablishmentAlreadyExistsException;
import com.cema.administration.exceptions.EstablishmentNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

class CemaExceptionHandlerTest {

    @Test
    public void handleEstablishmentAlreadyExistsExceptionShouldReturnResponseEntityWithMessageAndStatusCode() {
        CemaExceptionHandler cemaExceptionHandler = new CemaExceptionHandler();

        EstablishmentAlreadyExistsException ex = new EstablishmentAlreadyExistsException("Establishment 123 already exists");

        ResponseEntity<Object> result = cemaExceptionHandler.handleEstablishmentAlreadyExistsException(ex, null);
        ErrorResponse body = (ErrorResponse) result.getBody();
        HttpStatus status = result.getStatusCode();
        assertThat(body.getMessage(), is("Establishment Already Exists"));
        assertThat(body.getDetails(), is("Establishment 123 already exists"));
        assertThat(status, is(HttpStatus.CONFLICT));
    }

    @Test
    public void handleEstablishmentNotFoundExceptionShouldReturnResponseEntityWithMessageAndStatusCode(){
        CemaExceptionHandler cemaExceptionHandler = new CemaExceptionHandler();

        EstablishmentNotFoundException ex = new EstablishmentNotFoundException("Establishment 123 Not Found");

        ResponseEntity<Object> result = cemaExceptionHandler.handleEstablishmentNotFoundException(ex, null);
        ErrorResponse body = (ErrorResponse) result.getBody();
        HttpStatus status = result.getStatusCode();
        assertThat(body.getMessage(), is("Establishment Not Found"));
        assertThat(body.getDetails(), is("Establishment 123 Not Found"));
        assertThat(status, is(HttpStatus.NOT_FOUND));
    }

}