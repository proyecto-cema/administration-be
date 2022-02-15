package com.cema.administration.mapping;

public interface UpdateMappingService<ENTITY, DOMAIN> extends RegularMappingService<ENTITY, DOMAIN> {

    ENTITY updateDomainWithEntity(DOMAIN domain, ENTITY entity);
}
