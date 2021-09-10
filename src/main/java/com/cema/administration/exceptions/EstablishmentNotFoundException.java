package com.cema.administration.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EstablishmentNotFoundException extends RuntimeException {

    public EstablishmentNotFoundException(String message) {
        super(message);
    }

    public EstablishmentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public EstablishmentNotFoundException(Throwable cause) {
        super(cause);
    }

    public EstablishmentNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
