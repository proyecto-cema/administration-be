package com.cema.administration.mapping.impl;

import com.cema.administration.domain.SubscriptionType;
import com.cema.administration.entities.CemaSubscriptionType;
import com.cema.administration.mapping.UpdateMappingService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;

@Service
public class SubscriptionTypeMappingService implements UpdateMappingService<CemaSubscriptionType, SubscriptionType> {

    @Override
    public SubscriptionType mapEntityToDomain(CemaSubscriptionType cemaSubscriptionType) {
        return SubscriptionType.builder()
                .creationDate(cemaSubscriptionType.getCreationDate())
                .description(cemaSubscriptionType.getDescription())
                .duration(cemaSubscriptionType.getDuration())
                .expirationDate(cemaSubscriptionType.getExpirationDate())
                .name(cemaSubscriptionType.getName())
                .price(cemaSubscriptionType.getPrice())
                .build();
    }

    @Override
    public CemaSubscriptionType mapDomainToEntity(SubscriptionType subscriptionType) {
        return CemaSubscriptionType.builder()
                .creationDate(new Date())
                .duration(subscriptionType.getDuration())
                .expirationDate(subscriptionType.getExpirationDate())
                .description(subscriptionType.getDescription())
                .price(subscriptionType.getPrice())
                .name(subscriptionType.getName())
                .build();
    }

    @Override
    public CemaSubscriptionType updateDomainWithEntity(SubscriptionType subscriptionType, CemaSubscriptionType cemaSubscriptionType) {
        String name = StringUtils.hasText(subscriptionType.getName()) ? subscriptionType.getName() : cemaSubscriptionType.getName();
        String description = StringUtils.hasText(subscriptionType.getDescription()) ? subscriptionType.getDescription() : cemaSubscriptionType.getDescription();
        Date expirationDate = subscriptionType.getExpirationDate() != null ? subscriptionType.getExpirationDate() : cemaSubscriptionType.getExpirationDate();

        cemaSubscriptionType.setName(name);
        cemaSubscriptionType.setDescription(description);
        cemaSubscriptionType.setExpirationDate(expirationDate);
        return cemaSubscriptionType;
    }
}
