package com.cema.administration.mapping.impl;

import com.cema.administration.domain.Establishment;
import com.cema.administration.domain.Subscription;
import com.cema.administration.entities.CemaEstablishment;
import com.cema.administration.entities.CemaSubscription;
import com.cema.administration.mapping.UpdateMappingService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class EstablishmentMappingService implements UpdateMappingService<CemaEstablishment, Establishment> {

    private final SubscriptionMappingService subscriptionMappingService;

    public EstablishmentMappingService(SubscriptionMappingService subscriptionMappingService) {
        this.subscriptionMappingService = subscriptionMappingService;
    }

    @Override
    public Establishment mapEntityToDomain(CemaEstablishment cemaEstablishment) {
        List<CemaSubscription> cemaSubscriptions = cemaEstablishment.getSubscriptions();

        Optional<CemaSubscription> cemaSubscriptionOptional = cemaSubscriptions.stream().max(Comparator.comparing(CemaSubscription::getStartingDate));

        Subscription subscription = null;
        if (cemaSubscriptionOptional.isPresent()) {
            CemaSubscription cemaSubscription = cemaSubscriptionOptional.get();
            subscription = subscriptionMappingService.mapEntityToDomain(cemaSubscription);
        }

        return Establishment.builder()
                .name(cemaEstablishment.getName())
                .cuig(cemaEstablishment.getCuig())
                .email(cemaEstablishment.getEmail())
                .location(cemaEstablishment.getLocation())
                .phone(cemaEstablishment.getPhone())
                .ownerUserName(cemaEstablishment.getOwnerUserName())
                .activeSubscription(subscription)
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
