package com.cema.administration.services.database;

import com.cema.administration.entities.CemaSubscriptionType;
import org.springframework.data.domain.Page;

public interface DatabaseService {
    Page<CemaSubscriptionType> searchSubscriptionTypes(CemaSubscriptionType example, int page, int size);
}
