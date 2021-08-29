package com.cema.administration.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class EstablishmentAlreadyExistsException extends RuntimeException{

    public EstablishmentAlreadyExistsException() {
    }

    public EstablishmentAlreadyExistsException(String message) {
        super(message);
    }

    public EstablishmentAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public EstablishmentAlreadyExistsException(Throwable cause) {
        super(cause);
    }

    public EstablishmentAlreadyExistsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }


}
