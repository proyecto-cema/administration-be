package com.cema.administration.services.database.impl;

import com.cema.administration.entities.CemaSubscriptionType;
import com.cema.administration.repositories.SubscriptionTypeRepository;
import com.cema.administration.services.database.DatabaseService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DatabaseServiceImpl implements DatabaseService {

    private final SubscriptionTypeRepository subscriptionTypeRepository;

    public DatabaseServiceImpl(SubscriptionTypeRepository subscriptionTypeRepository) {
        this.subscriptionTypeRepository = subscriptionTypeRepository;
    }

    @Override
    public Page<CemaSubscriptionType> searchSubscriptionTypes(CemaSubscriptionType example, int page, int size) {
        ExampleMatcher caseInsensitiveExampleMatcher = ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Pageable paging = PageRequest.of(page, size, Sort.by("creation_date"));
        return subscriptionTypeRepository.findAll(Example.of(example, caseInsensitiveExampleMatcher), paging);
    }
}
