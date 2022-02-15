package com.cema.administration.repositories;

import com.cema.administration.entities.CemaAudit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditRepository extends JpaRepository<CemaAudit, Long> {

    Page<CemaAudit> findAllByEstablishmentCuig(String cuig, Pageable paging);
}
