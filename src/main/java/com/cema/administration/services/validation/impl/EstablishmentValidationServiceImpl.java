package com.cema.administration.services.validation.impl;

import com.cema.administration.domain.Establishment;
import com.cema.administration.services.validation.EstablishmentValidationService;
import org.springframework.stereotype.Service;

@Service
public class EstablishmentValidationServiceImpl implements EstablishmentValidationService {

    @Override
    public void validateEstablishmentForUsage(Establishment establishment) {
        //TODO Implement validation with subscription
    }
}
