package com.cema.administration.services.validation.impl;

import com.cema.administration.domain.Establishment;
import com.cema.administration.domain.Subscription;
import com.cema.administration.domain.SubscriptionType;
import com.cema.administration.exceptions.ValidationException;
import com.cema.administration.services.validation.EstablishmentValidationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class EstablishmentValidationServiceImpl implements EstablishmentValidationService {

    @Override
    public void validateEstablishmentForUsage(Establishment establishment) {
        Subscription subscription = establishment.getActiveSubscription();

        if (subscription == null || subscription.getSubscriptionType() == null) {
            throw new ValidationException("The establishment doesn't have a subscription associated.");
        }
        SubscriptionType subscriptionType = subscription.getSubscriptionType();
        long duration = subscriptionType.getDuration();
        LocalDateTime startingTime = subscription.getStartingDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        LocalDateTime endingTime = startingTime.plusDays(duration);
        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(endingTime)) {
            throw new ValidationException(String.format("The subscription has expired on %s", endingTime));
        }
    }
}
