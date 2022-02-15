package com.cema.administration.repositories;

import com.cema.administration.entities.CemaEstablishment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EstablishmentRepository extends JpaRepository<CemaEstablishment, Long> {

    CemaEstablishment findCemaEstablishmentByCuig(String cuig);
}
