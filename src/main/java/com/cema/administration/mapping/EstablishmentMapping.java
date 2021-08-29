package com.cema.administration.mapping;

import com.cema.administration.domain.Establishment;
import com.cema.administration.entities.CemaEstablishment;

public interface EstablishmentMapping {

    Establishment mapEntityToDomain(CemaEstablishment cemaEstablishment);

    CemaEstablishment mapDomainToEntity(Establishment establishment);

    CemaEstablishment updateDomainWithEntity(Establishment establishment, CemaEstablishment CemaEstablishment);
}
