package com.cema.administration.controllers;

import com.cema.administration.constants.Messages;
import com.cema.administration.domain.audit.Audit;
import com.cema.administration.entities.CemaAudit;
import com.cema.administration.exceptions.UnauthorizedException;
import com.cema.administration.mapping.RegularMappingService;
import com.cema.administration.repositories.AuditRepository;
import com.cema.administration.services.authorization.AuthorizationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1")
@Api(produces = "application/json", value = "Allows interaction with the audit database. V1")
@Validated
@Slf4j
public class AuditController {

    private static final String BASE_URL = "/audit/";

    private final AuditRepository auditRepository;
    private final RegularMappingService<CemaAudit, Audit> auditMappingService;
    private final AuthorizationService authorizationService;

    public AuditController(AuditRepository auditRepository, RegularMappingService<CemaAudit, Audit> auditMappingService, AuthorizationService authorizationService) {
        this.auditRepository = auditRepository;
        this.auditMappingService = auditMappingService;
        this.authorizationService = authorizationService;
    }

    @ApiOperation(value = "Register a new audit entry")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Audit created successfully"),
            @ApiResponse(code = 401, message = "Cannot audit outside users cuig")
    })
    @PostMapping(value = BASE_URL, produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> registerAudit(
            @ApiParam(
                    value = "Audit data to be inserted.")
            @RequestBody Audit audit) {

        log.info("Request to register new audit {}", audit);
        String cuig = audit.getEstablishmentCuig();
        if (!authorizationService.isOnTheSameEstablishment(cuig)) {
            throw new UnauthorizedException(String.format(Messages.OUTSIDE_ESTABLISHMENT, cuig));
        }

        CemaAudit newAudit = auditMappingService.mapDomainToEntity(audit);

        auditRepository.save(newAudit);

        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "Retrieve audits for your cuig", response = Audit.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Listed all audits", responseHeaders = {
                    @ResponseHeader(name = "total-elements", response = String.class, description = "Total number of search results"),
                    @ResponseHeader(name = "total-pages", response = String.class, description = "Total number of pages to navigate"),
                    @ResponseHeader(name = "current-page", response = String.class, description = "The page being returned, zero indexed")
            })
    })
    @GetMapping(value = BASE_URL + "list", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<Audit>> listAudits(
            @ApiParam(
                    value = "The page you want to retrieve.",
                    example = "1")
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @ApiParam(
                    value = "The maximum number of audit entries to return per page.",
                    example = "10")
            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {

        String cuig = authorizationService.getCurrentUserCuig();
        Pageable paging = PageRequest.of(page, size);

        Page<CemaAudit> cemaAuditPage;
        if (authorizationService.isAdmin()) {
            cemaAuditPage = auditRepository.findAll(paging);
        } else {
            cemaAuditPage = auditRepository.findAllByEstablishmentCuig(cuig, paging);
        }

        List<CemaAudit> cemaAudits = cemaAuditPage.getContent();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("total-elements", String.valueOf(cemaAuditPage.getTotalElements()));
        responseHeaders.set("total-pages", String.valueOf(cemaAuditPage.getTotalPages()));
        responseHeaders.set("current-page", String.valueOf(cemaAuditPage.getNumber()));

        List<Audit> audits = cemaAudits.stream().map(auditMappingService::mapEntityToDomain).collect(Collectors.toList());

        return ResponseEntity.ok().headers(responseHeaders).body(audits);
    }
}
