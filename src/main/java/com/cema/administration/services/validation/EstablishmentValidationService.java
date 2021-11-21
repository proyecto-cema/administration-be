package com.cema.administration.services.validation;

import com.cema.administration.domain.Establishment;

public interface EstablishmentValidationService {
    void validateEstablishmentForUsage(Establishment establishment);
}
