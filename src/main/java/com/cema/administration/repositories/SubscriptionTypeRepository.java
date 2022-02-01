package com.cema.administration.repositories;

import com.cema.administration.entities.CemaSubscriptionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionTypeRepository extends JpaRepository<CemaSubscriptionType, Long> {

    List<CemaSubscriptionType> findAllByNameIgnoreCase(String name);
}
