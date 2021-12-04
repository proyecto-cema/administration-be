package com.cema.administration.mapping;

import com.cema.administration.domain.audit.Audit;
import com.cema.administration.entities.CemaAudit;

public interface AuditMapping {
    Audit mapEntityToDomain(CemaAudit cemaAudit);

    CemaAudit mapDomainToEntity(Audit audit);
}
