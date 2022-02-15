package com.cema.administration.mapping.impl;

import com.cema.administration.domain.Subscription;
import com.cema.administration.entities.CemaSubscription;
import com.cema.administration.entities.CemaSubscriptionType;
import com.cema.administration.mapping.UpdateMappingService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class SubscriptionMappingService implements UpdateMappingService<CemaSubscription, Subscription> {

    private final SubscriptionTypeMappingService subscriptionTypeMappingService;

    public SubscriptionMappingService(SubscriptionTypeMappingService subscriptionTypeMappingService) {
        this.subscriptionTypeMappingService = subscriptionTypeMappingService;
    }

    @Override
    public Subscription mapEntityToDomain(CemaSubscription cemaSubscription) {
        CemaSubscriptionType cemaSubscriptionType = cemaSubscription.getCemaSubscriptionType();
        Date startingDate = cemaSubscription.getStartingDate();
        LocalDateTime startingTime = startingDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        LocalDateTime endingTime = startingTime.plusDays(cemaSubscriptionType.getDuration());
        Date endingDate = Date.from(endingTime.atZone(ZoneId.systemDefault()).toInstant());

        return Subscription.builder()
                .startingDate(startingDate)
                .endingDate(endingDate)
                .subscriptionType(subscriptionTypeMappingService.mapEntityToDomain(cemaSubscriptionType))
                .build();
    }

    @Override
    public CemaSubscription mapDomainToEntity(Subscription subscription) {
        return null;
    }

    @Override
    public CemaSubscription updateDomainWithEntity(Subscription subscription, CemaSubscription cemaSubscription) {
        return null;
    }
}
