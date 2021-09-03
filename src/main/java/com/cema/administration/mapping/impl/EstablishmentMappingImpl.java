package com.cema.administration.mapping.impl;

import com.cema.administration.domain.Establishment;
import com.cema.administration.entities.CemaEstablishment;
import com.cema.administration.mapping.EstablishmentMapping;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;

@Service
public class EstablishmentMappingImpl implements EstablishmentMapping {

    @Override
    public Establishment mapEntityToDomain(CemaEstablishment cemaEstablishment) {
        return Establishment.builder()
                .name(cemaEstablishment.getName())
                .cuig(cemaEstablishment.getCuig())
                .email(cemaEstablishment.getEmail())
                .location(cemaEstablishment.getLocation())
                .phone(cemaEstablishment.getPhone())
                .ownerUserName(cemaEstablishment.getOwnerUserName())
                .build();
    }

    @Override
    public CemaEstablishment mapDomainToEntity(Establishment establishment) {
        return CemaEstablishment.builder()
                .name(establishment.getName())
                .creationDate(new Date())
                .cuig(establishment.getCuig())
                .email(establishment.getEmail())
                .location(establishment.getLocation())
                .phone(establishment.getPhone())
                .ownerUserName(establishment.getOwnerUserName())
                .build();
    }

    @Override
    public CemaEstablishment updateDomainWithEntity(Establishment establishment, CemaEstablishment cemaEstablishment) {
        String name = StringUtils.hasText(establishment.getName()) ? establishment.getName() : cemaEstablishment.getName();
        String location = StringUtils.hasText(establishment.getLocation()) ? establishment.getLocation() : cemaEstablishment.getLocation();
        String email = StringUtils.hasText(establishment.getEmail()) ? establishment.getEmail() : cemaEstablishment.getEmail();
        String owner = StringUtils.hasText(establishment.getOwnerUserName()) ? establishment.getOwnerUserName() : cemaEstablishment.getOwnerUserName();
        String phone = StringUtils.hasText(establishment.getPhone()) ? establishment.getPhone() : cemaEstablishment.getPhone();
        cemaEstablishment.setName(name);
        cemaEstablishment.setLocation(location);
        cemaEstablishment.setEmail(email);
        cemaEstablishment.setOwnerUserName(owner);
        cemaEstablishment.setPhone(phone);
        return cemaEstablishment;
    }
}
