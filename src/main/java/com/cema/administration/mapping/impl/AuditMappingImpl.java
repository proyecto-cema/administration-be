package com.cema.administration.mapping.impl;

import com.cema.administration.domain.audit.Audit;
import com.cema.administration.entities.CemaAudit;
import com.cema.administration.mapping.AuditMapping;
import org.springframework.stereotype.Service;

@Service
public class AuditMappingImpl implements AuditMapping {

    @Override
    public Audit mapEntityToDomain(CemaAudit cemaAudit) {

        return Audit.builder()
                .establishmentCuig(cemaAudit.getEstablishmentCuig())
                .httpMethod(cemaAudit.getHttpMethod())
                .responseStatus(cemaAudit.getResponseStatus())
                .localAddress(cemaAudit.getLocalAddress())
                .method(cemaAudit.getMethod())
                .requestBody(cemaAudit.getRequestBody())
                .requestHeaders(cemaAudit.getRequestHeaders())
                .responseBody(cemaAudit.getResponseBody())
                .role(cemaAudit.getRole().replace("[", "").replace("]", ""))
                .uri(cemaAudit.getUri())
                .auditDate(cemaAudit.getAuditDate())
                .username(cemaAudit.getRequestorUsername())
                .module(cemaAudit.getModule())
                .build();
    }

    @Override
    public CemaAudit mapDomainToEntity(Audit audit) {
        return CemaAudit.builder()
                .auditDate(audit.getAuditDate())
                .establishmentCuig(audit.getEstablishmentCuig())
                .httpMethod(audit.getHttpMethod())
                .requestorUsername(audit.getUsername())
                .method(audit.getMethod())
                .requestBody(audit.getRequestBody())
                .role(audit.getRole().replace("[", "").replace("]", ""))
                .requestHeaders(audit.getRequestHeaders())
                .responseBody(audit.getResponseBody())
                .responseStatus(audit.getResponseStatus())
                .localAddress(audit.getLocalAddress())
                .uri(audit.getUri())
                .module(audit.getModule())
                .build();
    }
}
