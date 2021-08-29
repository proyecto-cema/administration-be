package com.cema.administration.mapping.impl;


import com.cema.administration.domain.Establishment;
import com.cema.administration.entities.CemaEstablishment;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class EstablishmentMappingImplTest {

    private final EstablishmentMappingImpl establishmentMapping = new EstablishmentMappingImpl();

    @Test
    public void mapEntityToDomainShouldReturnCorrectDomainObject() {
        String name = "name";
        String cuig = "cuig";
        String phone = "phone";
        String email = "email";
        String owner = "owner";
        String location = "location";
        CemaEstablishment cemaEstablishment = CemaEstablishment.builder()
                .name(name)
                .cuig(cuig)
                .phone(phone)
                .email(email)
                .ownerUserName(owner)
                .location(location)
                .build();

        Establishment result = establishmentMapping.mapEntityToDomain(cemaEstablishment);

        assertThat(result.getName(), is(name));
        assertThat(result.getCuig(), is(cuig));
        assertThat(result.getPhone(), is(phone));
        assertThat(result.getEmail(), is(email));
        assertThat(result.getOwnerUserName(), is(owner));
        assertThat(result.getLocation(), is(location));
    }

    @Test
    public void mapDomainToEntityShouldReturnCorrectEntityObject(){
        String name = "name";
        String cuig = "cuig";
        Date date = new Date();
        String phone = "phone";
        String email = "email";
        String owner = "owner";
        String location = "location";
        Establishment establishment = Establishment.builder()
                .name(name)
                .cuig(cuig)
                .phone(phone)
                .email(email)
                .ownerUserName(owner)
                .location(location)
                .build();

        CemaEstablishment result = establishmentMapping.mapDomainToEntity(establishment);

        assertThat(result.getName(), is(name));
        assertThat(result.getCuig(), is(cuig));
        assertThat(result.getPhone(), is(phone));
        assertThat(result.getEmail(), is(email));
        assertThat(result.getOwnerUserName(), is(owner));
        assertThat(result.getLocation(), is(location));
    }

    @Test
    public void updateDomainWithEntityShouldUpdateAllPresentFields(){
        String name = "name";
        String phone = "phone";
        String email = "email";
        String owner = "owner";
        String location = "location";
        Establishment establishment = Establishment.builder()
                .name(name)
                .phone(phone)
                .email(email)
                .ownerUserName(owner)
                .location(location)
                .build();

        CemaEstablishment cemaEstablishment = new CemaEstablishment();

        CemaEstablishment result= establishmentMapping.updateDomainWithEntity(establishment, cemaEstablishment);

        assertThat(result.getName(), is(name));
        assertThat(result.getPhone(), is(phone));
        assertThat(result.getEmail(), is(email));
        assertThat(result.getOwnerUserName(), is(owner));
        assertThat(result.getLocation(), is(location));
    }

    @Test
    public void updateDomainWithEntityShouldLeaveFieldsUntouchedIfNotPresent(){
        String name = "name";
        String phone = "phone";
        String email = "email";
        String owner = "owner";
        String location = "location";
        CemaEstablishment cemaEstablishment = CemaEstablishment.builder()
                .name(name)
                .phone(phone)
                .email(email)
                .ownerUserName(owner)
                .location(location)
                .build();

        Establishment establishment = Establishment.builder().build();

        CemaEstablishment result= establishmentMapping.updateDomainWithEntity(establishment, cemaEstablishment);

        assertThat(result.getName(), is(name));
        assertThat(result.getPhone(), is(phone));
        assertThat(result.getEmail(), is(email));
        assertThat(result.getOwnerUserName(), is(owner));
        assertThat(result.getLocation(), is(location));
    }

}