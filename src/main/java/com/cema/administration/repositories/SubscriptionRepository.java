package com.cema.administration.repositories;

import com.cema.administration.entities.CemaSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<CemaSubscription, Long> {
}
